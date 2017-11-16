package org.someth2say.taijitu.compare.equality;

@FunctionalInterface
public interface Categorizer<T> {
    int hashCode(T t);
}
