package org.someth2say.taijitu.compare.equality.structure;

public interface IComparableStructureEquality<T> extends IStructureEquality<T> {
    int compareTo(T first, T second);

    @Override
    ComparableStructureEqualityWrapper<T> wrap(T obj);

}
