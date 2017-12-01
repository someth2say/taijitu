package org.someth2say.taijitu.compare.equality.wrapper;

public interface Wrapper<T, EQ> {
    T getWrapped();

    EQ getEquality();

}
