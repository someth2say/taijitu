package org.someth2say.taijitu.compare.equality.composite;

public class CompositeEqualityWrapper<T> {

    private final T wrapped;
    private final ICompositeEquality<T> ICompositeEquality;

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

    public T getWrapped() {
        return wrapped;
    }

    public ICompositeEquality<T> getICompositeEquality() {
        return ICompositeEquality;
    }
}
