package buz.api.event;

import buz.api.event.Event;
import buz.api.EventPipeline;

public interface EventListener<E extends Event<?>> {
    void onEvent(EventPipeline<E> pipeline, E event);
}
