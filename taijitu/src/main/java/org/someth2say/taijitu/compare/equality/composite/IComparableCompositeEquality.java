package org.someth2say.taijitu.compare.equality.composite;

public interface IComparableCompositeEquality<T> extends ICompositeEquality<T> {
    int compareTo(T first, T second);

    @Override
    ComparableCompositeEqualityWrapper<T> wrap(T obj);

}
