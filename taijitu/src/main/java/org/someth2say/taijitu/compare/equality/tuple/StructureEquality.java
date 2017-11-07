package org.someth2say.taijitu.compare.equality.tuple;

public interface StructureEquality<T> {

    boolean equals(T obj, T other);

    int hashCode(T obj);

    StructureEqualityWrapper<T> wrap(T obj);

}
