package buz.api;

import buz.api.event.Event;
import buz.api.event.EventListener;

import java.util.Iterator;

public interface EventPipeline<E extends Event<?,?>> {
    Iterator<EventListener<E>> getRemainingListeners();

    void cancel();

    void unsubscribe();

    void launch(E event);
}
