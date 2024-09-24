package buz.impl.bus;

import buz.api.EventBus;
import buz.api.ScheduleType;
import buz.api.event.Event;
import buz.api.event.EventListener;
import buz.api.event.ResultListener;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class MultiThreadedEventBus<E extends Event<?>> implements EventBus<E> {
    private final Map<ScheduleType, EventBus<?>> busMap = new EnumMap<>(ScheduleType.class);
    private final Executor mainExecutor;
    private final Executor asyncExecutor;

    public MultiThreadedEventBus(Executor mainExecutor, Executor asyncExecutor) {
        this.mainExecutor = Objects.requireNonNull(mainExecutor);
        this.asyncExecutor = Objects.requireNonNull(asyncExecutor);
        for (ScheduleType type : ScheduleType.values()) {
            busMap.put(type, new HierarchyTypedEventBus<>());
        }
    }

    @Override
    public <A extends E> void postEvent(A event, boolean ignoreException, ResultListener<A> callback) {
        ((EventBus<A>) busMap.get(ScheduleType.CURRENT)).postEvent(event, ignoreException, callback);
        mainExecutor.execute(() -> {
            ((EventBus<A>) busMap.get(ScheduleType.MAIN)).postEvent(event, ignoreException, callback);
            asyncExecutor.execute(() -> {
                ((EventBus<A>) busMap.get(ScheduleType.ASYNC)).postEvent(event, ignoreException, callback);
            });
        });
    }

    @Override
    public <A extends E> void registerListener(int priority, ScheduleType scheduleType, Class<A> typeOfE, EventListener<A> listener) {
        Objects.requireNonNull(scheduleType, "scheduleType cannot be null");
        Objects.requireNonNull(typeOfE, "type of event cannot be null");
        Objects.requireNonNull(listener, "listener cannot be null");
        var bus = (EventBus<A>) busMap.get(scheduleType);
        bus.registerListener(priority, scheduleType, typeOfE, listener);
    }
}
