package org.someth2say.taijitu.compare.equality.stream;

import org.someth2say.taijitu.compare.equality.tuple.StructureEquality;

import java.util.Iterator;

public abstract class AbstractStreamEquality<T> implements StreamEquality<T> {

    final private StructureEquality<T> equality;
    final private StructureEquality<T> categorizer;

    protected AbstractStreamEquality(StructureEquality<T> equality, StructureEquality<T> categorizer) {
        this.equality = equality;
        this.categorizer = categorizer;
    }

    protected static <T> T getNextRecord(Iterator<T> resultSetSource) {
        return resultSetSource.hasNext() ? resultSetSource.next() : null;
    }

    public StructureEquality<T> getEquality() {
        return equality;
    }

    public StructureEquality<T> getCategorizer() {
        return categorizer;
    }
}
