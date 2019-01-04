package org.someth2say.taijitu.compare.equality.aspects.external;

import org.someth2say.taijitu.compare.result.Difference;
import org.someth2say.taijitu.compare.result.Missing;
import org.someth2say.taijitu.compare.result.Unequal;

import java.util.stream.Stream;

public interface Equalizer<EQUALIZED> {

    /**
     * Default implementation for checking equality delegates onto underlying differences.
     * In case no differences are found, objects are considered equal. Else, are considered non-equal.,
     * 
     * Note that this implementation creates and consume a `Stream` of underlying differences. 
     * Despite streams aer targeted to be lazy, efficiency depends on `underlyingDiffs` actual implementation and return type.
     * 
     * Equalizers for simple types (i.e. native types or value types) should overwrite this method and provide an straightforward implementation.
     */
    default boolean areEquals(EQUALIZED equalized1, EQUALIZED equalized2) {
        return underlyingDiffs(equalized1, equalized2) == null;
    }

    /**
     * Get the underlying differences that causes this equalizer to determine both elements are not areEquals.
     * The meaning for "underlying differences" depends on the kind of equalizer. I.E. composite equalizers delegate differences
     * to other equalizers, while stream equalizers apply the same equality to each element of the stream.
     * <p>
     * In case there is no difference between elements, null shall be returned.
     * <p>
     * If compared instances should be considered different, but no underlying differences (i.e. native types, Boolean or any other
     * type that can not be decomposed), and empty stream should be returned.
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
