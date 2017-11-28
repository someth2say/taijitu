package org.someth2say.taijitu.compare.equality;

import org.someth2say.taijitu.compare.result.Difference;
import org.someth2say.taijitu.compare.result.Mismatch;

import java.util.List;

public interface Equality<T> {
    default boolean equals(T t1, T t2) {
        return underlyingDiffs(t1, t2).isEmpty();
    }

    List<Mismatch<?>> underlyingDiffs(T t1, T t2);

    default Difference<T> asDifference(T t1, T t2) {
        List<Mismatch<?>> differences = underlyingDiffs(t1, t2);
        if (differences != null && !differences.isEmpty()) {
            return new Difference<T>(this, t1, t2, differences);
        } else {
            return null;
        }
    }
}
