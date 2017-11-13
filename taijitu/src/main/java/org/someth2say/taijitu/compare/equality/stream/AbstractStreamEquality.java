package org.someth2say.taijitu.compare.equality.stream;

import org.someth2say.taijitu.compare.equality.structure.IStructureEquality;

import java.util.Iterator;

public abstract class AbstractStreamEquality<T> implements StreamEquality<T> {

    final private IStructureEquality<T> equality;
    final private IStructureEquality<T> categorizer;

    protected AbstractStreamEquality(IStructureEquality<T> equality, IStructureEquality<T> categorizer) {
        this.equality = equality;
        this.categorizer = categorizer;
    }

    protected static <T> T getNextRecord(Iterator<T> resultSetSource) {
        return resultSetSource.hasNext() ? resultSetSource.next() : null;
    }

    public IStructureEquality<T> getEquality() {
        return equality;
    }

    public IStructureEquality<T> getCategorizer() {
        return categorizer;
    }
}
