package buz.impl;

import buz.api.event.Event;
import buz.api.event.EventListener;
import buz.impl.util.ForkableIterator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
public class RegisteredListener<E extends Event<?>> implements Comparable<RegisteredListener<E>>, Iterable<EventListener<E>> {
    private static final EventListener<?> NO_OP_LISTENER = (p, e) -> {
    };
    @Getter
    private final EventListener<E> listener;
    @Getter
    private final int priority;
    @Getter
    private final int typeDepth;
    //public RegisteredListener<E> fixedEnd;

    public RegisteredListener<E> next;
    public RegisteredListener<E> last;

    /**
     * This constructor is for eliminating null check overheads of head nodes.
     */
    @SuppressWarnings("unchecked")
    public RegisteredListener(int typeDepth, int priority) {
        this((EventListener<E>) NO_OP_LISTENER, priority, typeDepth); // like the top type.
    }
    public void insertSorted(RegisteredListener<E> listener) {
        if (listener.priority > priority) {
            RegisteredListener<E> tail = this;
            while (tail.next != null
                    && tail.next.priority <= listener.priority
                    && tail.next.typeDepth == this.typeDepth
            ) {
                tail = tail.next;
            }
            var prevNext = tail.next;
            tail.next = listener;
            listener.next = prevNext;
        } else {
            RegisteredListener<E> head = this;
            while (head.last != null
                    && head.last.priority >= listener.priority
                    && head.last.typeDepth == this.typeDepth
            ) {
                head = head.last;
            }
            var prevLast = head.last;
            head.last = listener;
            listener.last = prevLast;
        }
    }

    @Override
    public int compareTo(RegisteredListener<E> o) { //todo test
        var typeComp = Integer.compare(typeDepth, o.typeDepth);
        return typeComp == 0 ? Integer.compare(priority, o.priority) : typeComp;
    }

    @Override
    public ForkableIterator<EventListener<E>> iterator() {
        return new ListenerIterator<>(this);
    }

    @AllArgsConstructor
    public static class ListenerIterator<E extends Event<?>> implements ForkableIterator<EventListener<E>> {
        protected RegisteredListener<E> current;

        @Override
        public ForkableIterator<EventListener<E>> fork() {
            return new ListenerIterator<>(current); //todo deepcopy
        }

        @Override
        public boolean hasNext() {
            return current.next != null;
        }

        @Override
        public EventListener<E> next() {
            if (current.next == null) {
                throw new NoSuchElementException("This iterator is at the end");
            }
            return (current = current.next).listener;
        }

        @Override
        public void remove() {
            if (current.last != null) {
                current.last.next = current.next;
            }
            if (current.next != null) {
                current.next.last = current.last;
            }
        }
    }
}
