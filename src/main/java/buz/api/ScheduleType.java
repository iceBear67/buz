package buz.api;

import buz.api.event.Event;
import buz.api.event.EventListener;

public enum ScheduleType {
    /**
     * {@link buz.api.event.EventListener}s will be fired at the thread where {@link EventBus#postEvent(Event)} is called.
     * Listening on this thread MAY lead to delay in responsiveness. You shouldn't rely on this unless you know what are you doing.
     */
    CURRENT,
    /**
     * {@link buz.api.event.EventListener}s will be fired at the main thread, which is suitable for
     * middlewares (listeners with priority) and listeners that cares about event order since events are
     * posted by time order.
     */
    MAIN,
    /**
     * {@link buz.api.event.EventListener}s will be fired at an unspecified thread, like {@link Thread#ofVirtual()}.
     * By default, events are passed from its listener to super class listener and vice versa, and you may rely on its
     * order. However, in this case, you should use MAIN instead.
     * <p>
     * This is the default ScheduleType when using {@link EventBus#registerListener(Class, EventListener)}.
     */
    ASYNC
}
