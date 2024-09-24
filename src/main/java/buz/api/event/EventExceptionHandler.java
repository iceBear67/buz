package buz.api.event;

import buz.api.EventPipeline;

import java.util.function.BiConsumer;

/**
 * A type alias
 *
 * @param <E> type of event
 */
public interface EventExceptionHandler<E extends Event<?>> {
    /**
     *
     * @param t
     * @param u
     * @return should we recover
     */
    boolean accept(EventPipeline<E> t, Exception u);
}
