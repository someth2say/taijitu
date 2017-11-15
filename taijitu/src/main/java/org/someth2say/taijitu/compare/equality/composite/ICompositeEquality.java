package org.someth2say.taijitu.compare.equality.composite;

import org.someth2say.taijitu.compare.equality.composite.wrapper.CompositeEqualityWrapper;

public interface ICompositeEquality<T> {

    boolean equals(T obj, T other);

    int hashCode(T obj);

    CompositeEqualityWrapper<T> wrap(T obj);

}
