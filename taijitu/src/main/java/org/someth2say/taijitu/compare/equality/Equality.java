package org.someth2say.taijitu.compare.equality;

import org.someth2say.taijitu.compare.result.Difference;
import org.someth2say.taijitu.compare.result.Mismatch;

import java.util.List;

public interface Equality<T> {
    default boolean equals(T t1, T t2) {
        return differences(t1, t2).isEmpty();
    }

    List<Mismatch> differences(T t1, T t2);

    default Difference asDifference(T t1, T t2) {
        List<Mismatch> differences = differences(t1, t2);
        if (differences != null && !differences.isEmpty()) {
            return new Difference<>(this, t1, t2, differences);
        } else {
            return null;
        }
    }
}
