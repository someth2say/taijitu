package org.someth2say.taijitu.equality.explain;

import org.someth2say.taijitu.equality.aspects.external.Equalizer;

import java.util.stream.Stream;

/**
 * {@link ExplainingEqualizer} is an extension of {@link Equalizer} aspect that can provide with an explanation for why two objects are equals or not.
 * This explanation is provided as an streams of {@link Difference} instances.
 *
 * @see Difference
 * @param <TYPE> Type of objects to be compared
 * @param <DIFF_TYPE> Boundary for the parameter type of differences generated. 
 *      If TYPE is a simple type (i.e. Integer), DIFF_TYPE can be the same TYPE.
 *      If TYPE is a composite type (i.e. Address), DIFF_TYPE may be set to the more specific common ancestor for all components (Object, in the broadest case)
 *      If TYPE is an aggregator type (i.e. Stream<X> or Array<X>), DIFF_TYPE can be set to the aggregated type (X).
 * @see Difference
 * @see Missing
 * @see Unequal
 */
public interface ExplainingEqualizer<TYPE,DIFF_TYPE> extends Equalizer<TYPE> {
    /**
     * Get the underlying differences that causes this equalizer to determine both elements are not equal.
     * The meaning for "underlying differences" depends on the kind of equalizer. I.E. composite equalizers delegate differences
     * to other equalizers, while stream equalizers apply the same equality to each element of the stream.
     * <p>
     * In case there is no difference between elements (say, they are equal), empty stream shall be returned.
     * <p>
     * If compared instances should be considered different, but no underlying differences (i.e. native types, Boolean or any other
     * type that can not be decomposed), and empty stream should be returned.
     * <p>
     * Default implementation returns an empty stream.
     *
     * @param t1
     * @param t2
     * @return A stream of differences that explain the current difference
     */
    default Stream<Difference<DIFF_TYPE>> explain(TYPE t1, TYPE t2) {
        return Stream.empty();
    }
}