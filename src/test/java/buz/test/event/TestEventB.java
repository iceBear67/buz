package buz.test.event;

import buz.api.event.EventContext;
import buz.test.event.source.TestSource;

public class TestEventB extends TestEventA{
    public TestEventB(EventContext context, TestSource source) {
        super(context, source);
    }
}
