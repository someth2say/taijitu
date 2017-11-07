package org.someth2say.taijitu.compare.equality.tuple;

public class SortedStructureEqualityWrapper<T> extends StructureEqualityWrapper<T> implements Comparable<T> {

    public SortedStructureEqualityWrapper(T wrapped, SortedStructureEquality<T> sortedTupleEquality) {
        super(wrapped, sortedTupleEquality);
    }

    @Override
    public int compareTo(T other) {
        return ((SortedStructureEquality<T>) getStructureEquality()).compareTo(getWrapped(), other);
    }
}
