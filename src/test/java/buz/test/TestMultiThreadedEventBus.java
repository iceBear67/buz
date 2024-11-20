package buz.test;

import buz.api.EventBus;
import buz.api.ScheduleType;
import buz.impl.bus.MultiThreadedEventBus;
import buz.test.event.TestEventA;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class TestMultiThreadedEventBus {
    private EventBus<TestEventA> eventBus;

    @BeforeEach
    public void test() {
        eventBus = new MultiThreadedEventBus<>(
                Executors.newSingleThreadExecutor(r -> {
                    var thrd = new Thread(r);
                    thrd.setName("main");
                    return thrd;
                })
        );
    }

    @Test
    public void testScheduler() {
        AtomicBoolean called_main = new AtomicBoolean(false);
        AtomicBoolean called_current = new AtomicBoolean(false);
        var currentThrd = Thread.currentThread();
        eventBus.registerListener(0, ScheduleType.CURRENT, TestEventA.class, (p, e) -> {
            if (called_current.get()) throw new IllegalStateException("called_current called twice");
            called_current.set(currentThrd == Thread.currentThread());
        });
        eventBus.registerListener(0,ScheduleType.MAIN, TestEventA.class,(p,e)->{
            if (called_main.get()) throw new IllegalStateException("called_main called twice");
            called_main.set(Thread.currentThread().getName().equals("main"));
        });
        eventBus.postEvent(new TestEventA(null,null));
        Awaitility.await("test scheduling CURRENT").atMost(Duration.ofSeconds(1)).untilTrue(called_current);
        Awaitility.await("test scheduling MAIN").atMost(Duration.ofSeconds(1)).untilTrue(called_main);
    }

    @Test
    public void testOrder(){
        AtomicLong callAtCurrent = new AtomicLong();
        AtomicLong callAtMain = new AtomicLong();
        var currentThrd = Thread.currentThread();
        eventBus.registerListener(0, ScheduleType.CURRENT, TestEventA.class, (p, e) -> {
            callAtCurrent.set(System.nanoTime());
        });
        eventBus.registerListener(0,ScheduleType.MAIN, TestEventA.class,(p,e)->{
            callAtMain.set(System.nanoTime());
        });
        eventBus.postEvent(new TestEventA(null,null));
        Awaitility.await("test order CURRENT > MAIN").atMost(Duration.ofSeconds(1)).until(() -> callAtCurrent.get() < callAtMain.get());
    }
}
