package org.someth2say.taijitu.compare.equality.external;

public interface ExternalSortedEquality<T> extends ExternalEquality<T> {
    int compareTo(T obj, T other);

    @Override
    ComparisonWrapper<T> wrap(T obj);

}
