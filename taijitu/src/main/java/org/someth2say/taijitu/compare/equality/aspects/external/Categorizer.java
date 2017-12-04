package org.someth2say.taijitu.compare.equality.aspects.external;

@FunctionalInterface
public interface Categorizer<T> {
    int hashCode(T t);

}
