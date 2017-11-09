package org.someth2say.taijitu.compare.equality.value;

public interface ComparableValueEquality<T> extends ValueEquality<T> {

    int compare(T object1, T other);

}
