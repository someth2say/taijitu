package org.someth2say.taijitu.compare.equality.aspects.external;

@FunctionalInterface
public interface Hasher<T> {
    int hashCode(T t);

}
