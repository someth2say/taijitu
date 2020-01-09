package org.someth2say.taijitu.stream.mapping;

import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.equality.aspects.external.Hasher;
import org.someth2say.taijitu.equality.explain.Difference;
import org.someth2say.taijitu.equality.explain.Missing;
import org.someth2say.taijitu.equality.explain.Unequal;
import org.someth2say.taijitu.stream.StreamEqualizer;
import org.someth2say.taijitu.stream.StreamUtil;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Stream;

/**
 * Created by Jordi Sola on 02/03/2017.
 */
public class HashingStreamEqualizer<T> implements StreamEqualizer<T> {

    private static final Logger logger = LoggerFactory.getLogger(HashingStreamEqualizer.class);

    final private Hasher<T> hasher;

    public HashingStreamEqualizer(Hasher<T> hasher) {
        this.hasher = hasher;
    }

    @Override
    public Stream<Difference> explain(Stream<T> source, Stream<T> target) {

        final ArrayListValuedHashMap<Integer, T> map1 = new ArrayListValuedHashMap<>();
        final ArrayListValuedHashMap<Integer, T> map2 = new ArrayListValuedHashMap<>();

        Stream<Difference> mapA = source.flatMap(a -> map(a, hasher, map1, map2, (ta, tb) -> new Unequal<>(hasher, ta, tb)));
        Stream<Difference> mapB = target.flatMap(b -> map(b, hasher, map2, map1, (ta, tb) -> new Unequal<>(hasher, tb, ta)));
        Stream<Difference> unequals = StreamUtil.zip(mapA, mapB, true);

        Stream<Difference> remaining1 = Stream.generate(() -> {
            if (map1.isEmpty()) return null;
            Map.Entry<Integer, T> next = map1.entries().iterator().next();
            map1.removeMapping(next.getKey(), next.getValue());
            return (Difference) new Missing<>(hasher, next.getValue());
        }).takeWhile(Objects::nonNull);

        Stream<Difference> remaining2 = Stream.generate(() -> {
            if (map2.isEmpty()) return null;
            Map.Entry<Integer, T> next = map2.entries().iterator().next();
            map2.removeMapping(next.getKey(), next.getValue());
            return (Difference) new Missing<>(hasher, next.getValue());
        }).takeWhile(Objects::nonNull);

        return Stream.concat(unequals, Stream.concat(remaining1, remaining2));
    }

    private static <T> Stream<Difference> map(T thisT, Hasher<T> hasher,
                                              ArrayListValuedHashMap<Integer, T> thisMap,
                                              ArrayListValuedHashMap<Integer, T> otherMap,
                                              BiFunction<T, T, Difference<T>> diffMaker) {

        synchronized (otherMap) {
            if (otherMap.containsKey(hasher.hash(thisT))) {

                List<T> otherWithSameHash = otherMap.get(hasher.hash(thisT));

                for (T otherT : otherWithSameHash) {
                    // we have a key match ...
                    if (hasher.areEquals(thisT, otherT)) {
                        logger.trace("Found equal elements %s and %s", thisT, otherT);
                        otherMap.removeMapping(hasher.hash(thisT), otherT);
                        return Stream.empty();
                    }
                }

                //All elements with same hash are not equal, so we can raise a difference with any of them...
                T otherT = otherWithSameHash.get(0);
                logger.trace("Found difference between %s and %s", thisT, otherT);
                otherMap.removeMapping(hasher.hash(thisT), otherT);
                Difference<T> apply = diffMaker.apply(thisT, otherT);
                return Stream.of(apply);

            } else {
                logger.trace("Adding element to map: %s with key %d", thisT, hasher.hash(thisT));
                thisMap.put(hasher.hash(thisT), thisT);
                return Stream.empty();
            }
        }

    }

}
