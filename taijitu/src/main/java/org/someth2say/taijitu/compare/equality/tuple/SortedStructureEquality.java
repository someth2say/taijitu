package org.someth2say.taijitu.compare.equality.tuple;

public interface SortedStructureEquality<T> extends StructureEquality<T> {
    int compareTo(T obj, T other);

    @Override
    SortedStructureEqualityWrapper<T> wrap(T obj);

}
