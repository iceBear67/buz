package buz.api;

import buz.api.event.Event;
import buz.api.event.EventListener;
import buz.api.event.ResultListener;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public interface EventBus<E extends Event<?>> {
    /**
     * Post an event.
     *
     * @param event           the event to post
     * @param ignoreException should we interrupt for any exceptions? if false, then {@link CompletableFuture#exceptionally(Function)} won't be invoked.
     * @param callback
     */
    <A extends E> void postEvent(A event, boolean ignoreException, ResultListener<A> callback);

    /**
     * Post an event without interruption from exceptions
     *
     * @param event the event to post
     * @return future
     */
    default <A extends E> void postEvent(A event) {
        postEvent(event, true, null);
    }

    /**
     * Register a listener.
     *
     * @param priority     the lower, the earlier
     * @param scheduleType see {@link ScheduleType}
     * @param listener     who will be invoked
     */
    <A extends E> void registerListener(int priority, ScheduleType scheduleType, Class<A> typeOfE, EventListener<A> listener);

    /**
     * Register a listener on async dispatcher with normal priority.
     *
     * @param typeOfE  type of event
     * @param listener listener
     */
    default <A extends E> void registerListener(Class<A> typeOfE, EventListener<A> listener) {
        registerListener(EventPriorities.NORMAL, ScheduleType.ASYNC, typeOfE, listener);
    }

}
