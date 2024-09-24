package buz;

import buz.api.EventBus;
import buz.impl.bus.HierarchyTypedEventBus;
import buz.test.event.TestEventA;
import buz.test.event.TestEventB;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
public class BenchmarkEventbus {
    private EventBus<TestEventA> singleTypeBus;
    private EventBus<TestEventA> multipleTypeBus;
    private TestEventA eventA;
    private TestEventB eventB;
    private Blackhole bh;

    @Setup
    public void setup() {
        singleTypeBus = new HierarchyTypedEventBus<>();
        for (int i = 0; i < 1000; i++) {
            singleTypeBus.registerListener(TestEventA.class, (p, e) -> {

            });
        }
        multipleTypeBus = new HierarchyTypedEventBus<>();
        for (int i = 0; i < 1000; i++) {
            if (i % 2 == 0) {
                multipleTypeBus.registerListener(TestEventA.class, (p, e) -> {
                });
            } else {
                multipleTypeBus.registerListener(TestEventB.class, (p, e) -> {
                });
            }
        }
        eventA = new TestEventA(null, null);
        eventB = new TestEventB(null, null);
    }

    @Benchmark
    public void benchmarkSingleType() {
        singleTypeBus.postEvent(eventA, true, null);
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.INLINE)
    public void benchmarkMultipleType() {
        multipleTypeBus.postEvent(eventB, true, null);
    }
}
