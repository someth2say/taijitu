package org.someth2say.taijitu.compare.equality.wrapper;

public interface IWraper<T, EQ> {

    T unwrap();

    EQ getEquality();


}
