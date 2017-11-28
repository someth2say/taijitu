package org.someth2say.taijitu.compare.equality.wrapper;

import org.someth2say.taijitu.compare.equality.CategorizerEquality;

public class CategorizerEqualityWrapper<T> {

    private final T wrapped;
    private final CategorizerEquality<T> categorizerEquality;

    public CategorizerEqualityWrapper(T wrapped, CategorizerEquality<T> CategorizerEquality) {
        this.wrapped = wrapped;
        this.categorizerEquality = CategorizerEquality;
    }

    public T unwrapp() {
        return wrapped;
    }

    @Override
    public boolean equals(Object obj) {
        if (wrapped == null) {
            return obj == null;
        }
        if (obj instanceof CategorizerEqualityWrapper) {
            @SuppressWarnings("unchecked")
			CategorizerEqualityWrapper<T> otherWrapper = (CategorizerEqualityWrapper<T>) obj;
            T otherWrapped = otherWrapper.getWrapped();
            return categorizerEquality.equals(wrapped, otherWrapped);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return categorizerEquality.hashCode(wrapped);
    }

    T getWrapped() {
        return wrapped;
    }

    CategorizerEquality<T> getCategorizerEquality() {
        return categorizerEquality;
    }
}
