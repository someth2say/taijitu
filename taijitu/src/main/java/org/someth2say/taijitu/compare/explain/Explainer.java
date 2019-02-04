package org.someth2say.taijitu.compare.explain;

import java.util.stream.Stream;

public interface Explainer<EQUALIZED> {
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
     * @param equalized1
     * @param equalized2
     * @return A stream of differences that explain the current difference
     */
    default Stream<Difference> explain(EQUALIZED equalized1, EQUALIZED equalized2) {
        return Stream.empty();
    }
}