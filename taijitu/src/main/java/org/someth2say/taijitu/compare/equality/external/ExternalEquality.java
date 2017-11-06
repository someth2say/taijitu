package org.someth2say.taijitu.compare.equality.external;

public interface ExternalEquality<T> {

    boolean equals(T obj, T other);

    int hashCode(T obj);

    EqualityWrapper<T> wrap(T obj);

}
