package org.someth2say.taijitu.compare.equality.structure;

public class ComparableStructureEqualityWrapper<T> extends StructureEqualityWrapper<T> implements Comparable<T> {

    public ComparableStructureEqualityWrapper(T wrapped, IComparableStructureEquality<T> sortedTupleEquality) {
        super(wrapped, sortedTupleEquality);
    }

    @Override
    public int compareTo(T other) {
        return ((IComparableStructureEquality<T>) getIStructureEquality()).compareTo(getWrapped(), other);
    }
}
