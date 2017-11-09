package org.someth2say.taijitu.compare.equality.structure;

public interface ComparableStructureEquality<T> extends StructureEquality<T> {
    int compareTo(T first, T second);

    @Override
    ComparableStructureEqualityWrapper<T> wrap(T obj);

}
