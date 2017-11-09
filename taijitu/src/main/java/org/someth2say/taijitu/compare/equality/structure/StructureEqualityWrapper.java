package org.someth2say.taijitu.compare.equality.structure;

public class StructureEqualityWrapper<T> {

    private final T wrapped;
    private final StructureEquality<T> structureEquality;

    public StructureEqualityWrapper(T wrapped, StructureEquality<T> structureEquality) {
        this.wrapped = wrapped;
        this.structureEquality = structureEquality;
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
            return structureEquality.equals(wrapped, otherWrapped);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return structureEquality.hashCode(wrapped);
    }

    public T getWrapped() {
        return wrapped;
    }

    public StructureEquality<T> getStructureEquality() {
        return structureEquality;
    }
}
