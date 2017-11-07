package org.someth2say.taijitu.compare.equality.value;

public interface SortedValueEquality<T> extends ValueEquality<T> {

    int compare(T object1, T other);

}
