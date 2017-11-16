package org.someth2say.taijitu.compare.equality;

@FunctionalInterface
public interface Equality<T> {
    boolean equals(T t1, T t2);
}
