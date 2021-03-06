package org.someth2say.taijitu.stream;

import org.someth2say.taijitu.equality.explain.Difference;
import org.someth2say.taijitu.equality.explain.ExplainingEqualizer;

import java.util.stream.Stream;

/**
 * Created by Jordi Sola on 16/02/2017.
 * General contract interface for comparing streams.
 * Streams are a edge case, because comparing them means consuming them.
 * Hence, usual use cases will better get an explanation on the differences, instead of just indicating they are equal or not.
 * That's the reason 'StreamEqualizer' extends 'ExplainingEqualizer' interface.
 *
 * For the same reason, StreamComparer or StreamHasher are not implemented: no use on  compare streams or calculate hash, as they are consumed and became unusable.
 *
 */
public interface StreamEqualizer<TYPE> extends ExplainingEqualizer<Stream<TYPE>,TYPE> {
    /**
     * By default, all stream equalities will delegate on explain.
     * That means both streams will be consumed upon comparing them.
     *
     * @param equalized1
     * @param equalized2
     * @return
     */
    @Override
    default boolean areEquals(Stream<TYPE> equalized1, Stream<TYPE> equalized2) {
        Stream<Difference<TYPE>> underlyingDiffs = explain(equalized1, equalized2);
        return !underlyingDiffs.findAny().isPresent();
    }
}
