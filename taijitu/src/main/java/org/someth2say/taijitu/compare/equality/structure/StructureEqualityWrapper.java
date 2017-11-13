package org.someth2say.taijitu.compare.equality.structure;

public class StructureEqualityWrapper<T> {

    private final T wrapped;
    private final IStructureEquality<T> IStructureEquality;

    public StructureEqualityWrapper(T wrapped, IStructureEquality<T> IStructureEquality) {
        this.wrapped = wrapped;
        this.IStructureEquality = IStructureEquality;
    }

    public T unwrapp() {
        return wrapped;
    }

    @Override
    public boolean equals(Object obj) {
        if (wrapped == null) {
            return obj == null;
        }
        if (obj instanceof StructureEqualityWrapper) {
            StructureEqualityWrapper<T> otherWrapper = (StructureEqualityWrapper<T>) obj;
            T otherWrapped = otherWrapper.getWrapped();
            return IStructureEquality.equals(wrapped, otherWrapped);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return IStructureEquality.hashCode(wrapped);
    }

    public T getWrapped() {
        return wrapped;
    }

    public IStructureEquality<T> getIStructureEquality() {
        return IStructureEquality;
    }
}
