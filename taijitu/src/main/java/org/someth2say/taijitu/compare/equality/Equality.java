package org.someth2say.taijitu.compare.equality;

import org.someth2say.taijitu.compare.result.Mismatch;

import java.util.Collection;

public interface Equality<T> {
    default boolean equals(T t1, T t2) {
        return differences(t1, t2).isEmpty();
    }

    Collection<Mismatch> differences(T t1, T t2);
}
