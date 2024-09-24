package buz.test;

import buz.api.EventBus;
import buz.impl.bus.HierarchyTypedEventBus;
import buz.test.event.TestEventA;
import buz.test.event.TestEventB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static org.awaitility.Awaitility.await;

class TestHierarchyTypedEventBus {
    private EventBus<TestEventA> bus;

    @BeforeEach
    public void setupBus() {
        bus = new HierarchyTypedEventBus<>();
    }

    @Test
    public void testSingleType() {
        AtomicBoolean isReady = new AtomicBoolean(false);
        AtomicBoolean isReady2 = new AtomicBoolean(false);
        var evt = new TestEventA(null, null);
        bus.registerListener(TestEventA.class, (p, e) -> {
            isReady.set(e == evt);
        });
        bus.registerListener(TestEventA.class, (p, e) -> {
            isReady2.set(e == evt);
        });
        bus.postEvent(evt);
        await().atMost(Duration.ofSeconds(1)).untilTrue(isReady);
    }

    @Test
    public void testHierarchyType() {
        AtomicLong atChild = new AtomicLong();
        AtomicLong atParent = new AtomicLong();
        var evtB = new TestEventB(null, null);
        var evtA = new TestEventA(null, null);
        bus.registerListener(TestEventA.class, (p, e) -> {
            atParent.set(System.nanoTime());
        });
        bus.registerListener(TestEventB.class, (p, e) -> {
            atChild.set(System.nanoTime());
        });
        bus.postEvent(evtB);
        await().atMost(Duration.ofSeconds(1)).until(() -> atParent.get() != 0 && atParent.get() > atChild.get());

    }
}
