package buz.test.event;

import buz.api.event.Event;
import buz.api.event.EventContext;
import buz.test.event.source.TestSource;

public class TestEventA extends Event<TestSource> {
    public TestEventA(EventContext context, TestSource source) {
        super(context, source);
    }
}
