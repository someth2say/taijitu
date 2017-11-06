package org.someth2say.taijitu.compare.equality.external;

public class ComparisonWrapper<T> extends EqualityWrapper<T> implements Comparable<T> {

    public ComparisonWrapper(T wrapped, ExternalSortedEquality<T> externalSortedEquality) {
        super(wrapped, externalSortedEquality);
    }

    @Override
    public int compareTo(T other) {
        return ((ExternalSortedEquality<T>) getExternalEquality()).compareTo(getWrapped(), other);
    }
}
