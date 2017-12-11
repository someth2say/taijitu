package org.someth2say.taijitu.compare.equality.aspects.external;

import org.someth2say.taijitu.compare.equality.wrapper.EqualizableWrapper;
import org.someth2say.taijitu.compare.equality.wrapper.IEqualizableWraper;
import org.someth2say.taijitu.compare.result.Difference;
import org.someth2say.taijitu.compare.result.Missing;
import org.someth2say.taijitu.compare.result.Unequal;

import java.util.List;

public interface Equalizer<T> {

    default IEqualizableWraper<T,?> wrap(T obj) {
        return new EqualizableWrapper<>(obj, this);
    }

    default boolean equals(T t1, T t2) {
        return underlyingDiffs(t1, t2).isEmpty();
    }

    List<Difference<?>> underlyingDiffs(T t1, T t2);

    default Unequal<T> asDifference(T t1, T t2) {
        List<Difference<?>> differences = underlyingDiffs(t1, t2);
        if (differences != null && !differences.isEmpty()) {
            return new Unequal<>(this, t1, t2, differences);
        } else {
            return null;
        }
    }

    default Missing<T> asMissing(T t1) {
        return new Missing<>(this, t1);
    }
}
