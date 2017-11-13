package org.someth2say.taijitu.compare.equality.structure;

public interface IStructureEquality<T,Q> {

    boolean equals(T obj, Q other);

    int hashCode(T obj);

    StructureEqualityWrapper<T> wrap(T obj);

}
