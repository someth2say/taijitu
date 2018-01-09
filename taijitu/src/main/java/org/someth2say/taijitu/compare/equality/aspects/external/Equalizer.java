package org.someth2say.taijitu.compare.equality.aspects.external;

import org.someth2say.taijitu.compare.result.Difference;
import org.someth2say.taijitu.compare.result.Missing;
import org.someth2say.taijitu.compare.result.Unequal;

import java.util.stream.Stream;

public interface Equalizer<EQUALIZED> {

    default boolean areEquals(EQUALIZED equalized1, EQUALIZED equalized2) {
        return underlyingDiffs(equalized1, equalized2).count() == 0;
    }

    /**
     * Get the underlying differences that causes this equalizer to determine both elements are not areEquals.
     * The meaning for "underlying differences" depends on the kind of equalizer. I.E. composite equalizers delegate differences
     * to other equalizers, while stream equalizers apply the same equality to each element of the stream.
     * <p>
     * In case there is no difference between elements, null shall be returned.
     *
     * @param equalized1
     * @param equalized2
     * @return A stream of differences that explain the current differenceOrNull
     */
    Stream<Difference<?>> underlyingDiffs(EQUALIZED equalized1, EQUALIZED equalized2);

    default Unequal<EQUALIZED> asUnequal(EQUALIZED equalized1, EQUALIZED equalized2) {
        Stream<Difference<?>> underlyingDifferences = underlyingDiffs(equalized1, equalized2);
        if (underlyingDifferences != null)
            return new Unequal<>(this, equalized1, equalized2, underlyingDifferences);
        else
            return null;
    }

    default Missing<EQUALIZED> asMissing(EQUALIZED equalized1) {
        return new Missing<>(this, equalized1);
    }
}
