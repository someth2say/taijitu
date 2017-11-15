package org.someth2say.taijitu.compare.equality.composite.wrapper;

import org.someth2say.taijitu.compare.equality.composite.ICompositeEquality;

public class CompositeEqualityWrapper<T> {

    private final T wrapped;
    private final org.someth2say.taijitu.compare.equality.composite.ICompositeEquality<T> ICompositeEquality;

    public CompositeEqualityWrapper(T wrapped, ICompositeEquality<T> ICompositeEquality) {
        this.wrapped = wrapped;
        this.ICompositeEquality = ICompositeEquality;
    }

    public T unwrapp() {
        return wrapped;
    }

    @Override
    public boolean equals(Object obj) {
        if (wrapped == null) {
            return obj == null;
        }
        if (obj instanceof CompositeEqualityWrapper) {
            CompositeEqualityWrapper<T> otherWrapper = (CompositeEqualityWrapper<T>) obj;
            T otherWrapped = otherWrapper.getWrapped();
            return ICompositeEquality.equals(wrapped, otherWrapped);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return ICompositeEquality.hashCode(wrapped);
    }

    T getWrapped() {
        return wrapped;
    }

    ICompositeEquality<T> getICompositeEquality() {
        return ICompositeEquality;
    }
}
