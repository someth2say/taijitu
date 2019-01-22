package org.someth2say.taijitu.compare.equality.impl.stream;

import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.compare.result.Difference;

import java.util.stream.Stream;

/**
 * Created by Jordi Sola on 16/02/2017.
 */
//TODO: What about StreamComparer<T> or StreamHasher<T>?
public interface StreamEqualizer<T> extends Equalizer<Stream<T>> {
    /**
     * By default, all stream equalities will delegate on explain.
     * That means both streams will be consumed upon comparing them.
     *
     * @param equalized1
     * @param equalized2
     * @return
     */
    @Override
    default boolean areEquals(Stream<T> equalized1, Stream<T> equalized2) {
        Stream<Difference> underlyingDiffs = explain(equalized1, equalized2);
        return !underlyingDiffs.findAny().isPresent();
    }
}
