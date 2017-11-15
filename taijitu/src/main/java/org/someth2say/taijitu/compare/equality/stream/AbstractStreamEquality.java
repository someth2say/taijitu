package org.someth2say.taijitu.compare.equality.stream;

import org.someth2say.taijitu.compare.equality.composite.ICompositeEquality;

import java.util.Iterator;

public abstract class AbstractStreamEquality<T> implements StreamEquality<T> {

    final private ICompositeEquality<T> equality;
    //TODO: Generify this to a collection of Categorizers/Comparers
    final private ICompositeEquality<T> categorizer;

    protected AbstractStreamEquality(ICompositeEquality<T> equality, ICompositeEquality<T> categorizer) {
        this.equality = equality;
        this.categorizer = categorizer;
    }

    protected static <T> T getNextRecordOrNull(Iterator<T> resultSetSource) {
        return resultSetSource.hasNext() ? resultSetSource.next() : null;
    }

    protected ICompositeEquality<T> getEquality() {
        return equality;
    }

    protected ICompositeEquality<T> getCategorizer() {
        return categorizer;
    }
}
