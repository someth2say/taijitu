package org.someth2say.taijitu.compare.equality.stream;

import org.someth2say.taijitu.compare.equality.Equality;

import java.util.Iterator;

public abstract class AbstractStreamEquality<T, E> {

    final private Equality<T> equality;
    final private E other;

    protected AbstractStreamEquality(Equality<T> equality, E other) {
        this.equality = equality;
        this.other = other;
    }

    protected static <T> T getNextRecordOrNull(Iterator<T> resultSetSource) {
        return resultSetSource.hasNext() ? resultSetSource.next() : null;
    }

    protected Equality<T> getEquality() {
        return equality;
    }

    protected E getOther() {
        return other;
    }
}
