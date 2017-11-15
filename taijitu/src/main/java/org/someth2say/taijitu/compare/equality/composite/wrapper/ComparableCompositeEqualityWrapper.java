package org.someth2say.taijitu.compare.equality.composite.wrapper;

import org.someth2say.taijitu.compare.equality.composite.IComparableCompositeEquality;

public class ComparableCompositeEqualityWrapper<T> extends CompositeEqualityWrapper<T> implements Comparable<T> {

    public ComparableCompositeEqualityWrapper(T wrapped, IComparableCompositeEquality<T> sortedTupleEquality) {
        super(wrapped, sortedTupleEquality);
    }

    @Override
    public int compareTo(T other) {
        return ((IComparableCompositeEquality<T>) getICompositeEquality()).compareTo(getWrapped(), other);
    }
}
