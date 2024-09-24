package buz.impl.util;

import java.util.Iterator;

public interface ForkableIterator<E> extends Iterator<E> {
    /**
     * @return a copy of current iterator.
     */
    ForkableIterator<E> fork();
}
