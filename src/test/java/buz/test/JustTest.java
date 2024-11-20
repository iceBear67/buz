package buz.test;

import buz.impl.bus.generator.SingleTypedEventBusGenerator;
import buz.test.event.TestEventA;

public class JustTest {
    public static void main(String[] args) {
        new SingleTypedEventBusGenerator<>().createFor(TestEventA.class);
    }
}
