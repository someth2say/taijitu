package org.someth2say.taijitu.compare.equality.aspects.external;

import org.someth2say.taijitu.compare.equality.wrapper.EqualizableWrapper;
import org.someth2say.taijitu.compare.equality.wrapper.IEqualizableWraper;
import org.someth2say.taijitu.compare.result.Difference;
import org.someth2say.taijitu.compare.result.Missing;
import org.someth2say.taijitu.compare.result.Unequal;

import java.util.stream.Stream;

public interface Equalizer<T> {

    default IEqualizableWraper<T, ?> wrap(T obj) {
        return new EqualizableWrapper<>(obj, this);
    }

    default boolean equals(T t1, T t2) {
        return underlyingDiffs(t1, t2).count() == 0;
    }

    /**
     * Get the underlying differences that causes this equalizer to determine both elements are not equals.
     * The meaning for "underlying differences" depends on the kind of equalizer. I.E. composite equalizers delegate differences
     * to other equalizers, while stream equalizers apply the same equality to each element of the stream.
     * <p>
     * In case there is no difference between elements, null shall be returned.
     *
     * @param t1
     * @param t2
     * @return A stream of differences that explain the current differenceOrNull
     */
    Stream<Difference<?>> underlyingDiffs(T t1, T t2);

    default Unequal<T> asUnequal(T t1, T t2) {
        Stream<Difference<?>> underlyingDifferences = underlyingDiffs(t1, t2);
        if (underlyingDifferences != null)
            return new Unequal<>(this, t1, t2, underlyingDifferences);
        else
            return null;
    }

    default Missing<T> asMissing(T t1) {
        return new Missing<>(this, t1);
    }
}
