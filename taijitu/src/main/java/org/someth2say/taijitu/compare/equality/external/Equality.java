package org.someth2say.taijitu.compare.equality.external;

import org.someth2say.taijitu.compare.equality.internal.Equalizable;
import org.someth2say.taijitu.compare.equality.wrapper.EqualityWrapper;
import org.someth2say.taijitu.compare.result.Difference;
import org.someth2say.taijitu.compare.result.Mismatch;

import java.util.List;

public interface Equality<T> {

    default Equalizable<T,? extends Equality<T>> wrap(T obj) {
        return new EqualityWrapper<>(obj, this);
    }

    default boolean equals(T t1, T t2) {
        return underlyingDiffs(t1, t2).isEmpty();
    }

    List<Mismatch<?>> underlyingDiffs(T t1, T t2);

    default Difference<T> asDifference(T t1, T t2) {
        List<Mismatch<?>> differences = underlyingDiffs(t1, t2);
        if (differences != null && !differences.isEmpty()) {
            return new Difference<>(this, t1, t2, differences);
        } else {
            return null;
        }
    }
}
