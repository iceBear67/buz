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
    MAIN
}
