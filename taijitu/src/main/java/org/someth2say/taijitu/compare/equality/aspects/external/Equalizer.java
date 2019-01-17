package org.someth2say.taijitu.compare.equality.aspects.external;

import org.someth2say.taijitu.compare.result.Difference;
import org.someth2say.taijitu.compare.result.Missing;
import org.someth2say.taijitu.compare.result.Unequal;

import java.util.Optional;
import java.util.stream.Stream;

public interface Equalizer<EQUALIZED> {

    /**
     * Equalizers should implement `areEquals` method to define the equality definition they represent.
     * Straightforward implementations will directly compare the arguments values, and return true iif they are equals (based onb represented equality definition).
     * <p>
     * Some implementation may extract some values (members) from arguments, and compare them. This approach can be performed directly in this implementation, or can be
     * built at run-time by partial equalities.
     * <p>
     * Other implementations may delegate in 'underlying differences' (underlyingDiffs method). But be aware that there are two situations where 'underlyingDiffs' will return
     * an empty stream: 1) when both elements are equals and 2) when there are no underlying elements to compare.
     */
    boolean areEquals(EQUALIZED equalized1, EQUALIZED equalized2);

    /**
     * Get the underlying differences that causes this equalizer to determine both elements are not areEquals.
     * The meaning for "underlying differences" depends on the kind of equalizer. I.E. composite equalizers delegate differences
     * to other equalizers, while stream equalizers apply the same equality to each element of the stream.
     * <p>
     * In case there is no difference between elements, empty stream shall be returned.
     * <p>
     * If compared instances should be considered different, but no underlying differences (i.e. native types, Boolean or any other
     * type that can not be decomposed), and empty stream should be returned.
     *
     * @param equalized1
     * @param equalized2
     * @return A stream of differences that explain the current difference
     */
    default Stream<Difference> underlyingDiffs(EQUALIZED equalized1, EQUALIZED equalized2) {
        return Stream.empty();
    }


    /**
     * This is a hairy method. It will return an optional that will be empty if elements are equal, but with an 'unequal' if elements are not equal.
     *
     * @param equalized1
     * @param equalized2
     * @return
     */
    @Deprecated
    default Optional<Unequal<EQUALIZED>> asUnequal(EQUALIZED equalized1, EQUALIZED equalized2) {
        // TODO: Not happy at all with this. 'underlyingDiffs' may not be available if 'areEquals' have already consumed the compared elements!
        if (areEquals(equalized1, equalized2)) {
            return Optional.of(new Unequal<>(this, equalized1, equalized2, underlyingDiffs(equalized1, equalized2)));
        } else
            return Optional.empty();
    }

    //TODO: Use an Optional, instead of a stream.
    default Stream<Missing<EQUALIZED>> asMissing(EQUALIZED equalized1) {
        return Stream.of(new Missing<>(this, equalized1));
    }
}
