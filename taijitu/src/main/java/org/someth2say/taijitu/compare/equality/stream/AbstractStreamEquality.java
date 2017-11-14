package org.someth2say.taijitu.compare.equality.stream;

import org.someth2say.taijitu.compare.equality.composite.ICompositeEquality;

import java.util.Iterator;

public abstract class AbstractStreamEquality<T> implements StreamEquality<T> {

    final private ICompositeEquality<T> equality;
    final private ICompositeEquality<T> categorizer;

    protected AbstractStreamEquality(ICompositeEquality<T> equality, ICompositeEquality<T> categorizer) {
        this.equality = equality;
        this.categorizer = categorizer;
    }

    protected static <T> T getNextRecord(Iterator<T> resultSetSource) {
        return resultSetSource.hasNext() ? resultSetSource.next() : null;
    }

    public ICompositeEquality<T> getEquality() {
        return equality;
    }

    public ICompositeEquality<T> getCategorizer() {
        return categorizer;
    }
}
