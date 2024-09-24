package buz.impl.bus;

import buz.api.event.EventExceptionHandler;
import buz.api.event.EventListener;
import buz.api.ScheduleType;
import buz.api.event.Event;
import buz.api.EventBus;
import buz.api.event.ResultListener;
import buz.impl.IteratorBasedPipeline;
import buz.impl.RegisteredListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * An implementation of {@link EventBus}, which has weak thread safety and should be pinned on thread.
 * Currently, generic types are not supprted.
 *
 * @param <E> type of event
 */
public class HierarchyTypedEventBus<E extends Event<?>> implements EventBus<E> { //todo fix type
    private static final EventExceptionHandler<?> ALWAYS_FALSE = (a, b) -> false;
    private static final EventExceptionHandler<?> ALWAYS_TRUE = (a, b) -> true;
    private final Map<Class<?>, RegisteredListener<?>> typedListeners = new HashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <A extends E> void postEvent(A event, boolean ignoreException, ResultListener<A> callback) {
        var listener = (RegisteredListener<A>) getListenerByType(event.getClass());
        postEventAtNode(listener, event, ignoreException, callback);
    }

    @SuppressWarnings("unchecked")
    protected <E extends Event<?>> RegisteredListener<E> getListenerByType(Class<E> typeOfEvent) {
        if (typedListeners.containsKey(typeOfEvent)) {
            return (RegisteredListener<E>) typedListeners.get(typeOfEvent);
        }
        var depth = calcTypeDepth(typeOfEvent);
        var headForType = new RegisteredListener<E>(depth, Integer.MIN_VALUE);
        var sc = typeOfEvent.getSuperclass();
        if (Event.class.isAssignableFrom(sc)) {
            var parent = getListenerByType((Class<E>) sc);
            var intermediate = new RegisteredListener<>(depth, Integer.MAX_VALUE);
            headForType.insertSorted((RegisteredListener<E>) intermediate);
            intermediate.next = (RegisteredListener<Event<?>>) parent; // a trick.
        }
        typedListeners.put(typeOfEvent, headForType);
        return headForType;
    }

    /**
     * {@link Object} is not considered in this value.
     */
    private static int calcTypeDepth(Class<?> type) {
        int i;
        for (i = 0; (type = type.getSuperclass()) != null; i++) {
        }
        return i;
    }

    private <E extends Event<?>> void postEventAtNode(RegisteredListener<E> headNode, E event, boolean ignoreException, ResultListener<E> callback) {
        EventExceptionHandler<E> exceptionHandler = (EventExceptionHandler<E>) (ignoreException ? ALWAYS_TRUE : ALWAYS_FALSE);
        var pipeline = new IteratorBasedPipeline<>(headNode.iterator(), callback, exceptionHandler);
        pipeline.launch(event);
    }

    @Override
    public <A extends E> void registerListener(int priority, ScheduleType scheduleType, Class<A> typeOfE, EventListener<A> listener) {
        Objects.requireNonNull(typeOfE, "type of event cannot be null");
        Objects.requireNonNull(listener, "listener cannot be null");
        getListenerByType(typeOfE).insertSorted(new RegisteredListener<>(
                listener, priority, calcTypeDepth(typeOfE)
        ));
    }
}
