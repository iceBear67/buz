package buz.impl;

import buz.api.event.Event;
import buz.api.event.EventExceptionHandler;
import buz.api.event.EventListener;
import buz.api.EventPipeline;
import buz.api.event.ResultListener;
import buz.impl.util.ForkableIterator;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class IteratorBasedPipeline<E extends Event<?,?>> implements EventPipeline<E> {
    public static final EventExceptionHandler<?> NO_OP_EXCEPTION_HANDLER = (p, e) -> false;
    protected final ForkableIterator<EventListener<E>> iterator;
    protected final ResultListener<E> future;
    protected final EventExceptionHandler<E> exceptionHandler;
    protected Exception lastException;

    @Override
    public Iterator<EventListener<E>> getRemainingListeners() {
        return iterator.fork();
    }

    @Override
    public void cancel() {
        throw new EventCancelException();
    }

    @Override
    public void unsubscribe() {
        iterator.remove();
    }

    @Override
    public void launch(E deliveringEvent) {
        boolean cancelled = false;
        // this boosts performance in openjdk, about 160000->200000op/s but slows down in graalvm, 733000 -> 700000
        final var iterator = this.iterator;
        try {
            while (iterator.hasNext()) {
                invoke(iterator, deliveringEvent);
            }
        } catch (EventCancelException ignored) {
            // just continue.
            cancelled = true;
        } catch (Exception e) {
            e.printStackTrace();  //todo loggers
            lastException = e;
            if (exceptionHandler.accept(this, e)) {
                launch(deliveringEvent);
                return;
            }
        }
        if (future == null) return;
        future.onResult(deliveringEvent, cancelled, lastException);
    }

    private void invoke(ForkableIterator<EventListener<E>> iterator, E deliveringEvent) {
        iterator.next().onEvent(this, deliveringEvent);
    }

    private static class EventCancelException extends RuntimeException {
        @Override
        public synchronized Throwable fillInStackTrace() {
            return this;
        }
    }
}
