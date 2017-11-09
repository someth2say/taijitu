package org.someth2say.taijitu.compare.equality.structure;

public class ComparableStructureEqualityWrapper<T> extends StructureEqualityWrapper<T> implements Comparable<T> {

    public ComparableStructureEqualityWrapper(T wrapped, ComparableStructureEquality<T> sortedTupleEquality) {
        super(wrapped, sortedTupleEquality);
    }

    @Override
    public int compareTo(T other) {
        return ((ComparableStructureEquality<T>) getStructureEquality()).compareTo(getWrapped(), other);
    }
}
