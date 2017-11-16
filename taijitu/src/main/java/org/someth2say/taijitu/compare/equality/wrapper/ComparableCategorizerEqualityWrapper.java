package org.someth2say.taijitu.compare.equality.wrapper;

import org.someth2say.taijitu.compare.equality.ComparableCategorizerEquality;

public class ComparableCategorizerEqualityWrapper<T> implements Comparable<T> {
    private final T wrapped;
    private final ComparableCategorizerEquality<T> categorizerEquality;

    public ComparableCategorizerEqualityWrapper(T wrapped, ComparableCategorizerEquality<T> categorizerEquality) {
        this.wrapped = wrapped;
        this.categorizerEquality = categorizerEquality;
    }

    @Override
    public int compareTo(T other) {
        return getEquality().compare(getWrapped(), other);
    }

    public ComparableCategorizerEquality<T> getEquality() {
        return categorizerEquality;
    }

    public T getWrapped() {
        return wrapped;
    }
}
