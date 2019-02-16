package org.someth2say.taijitu.stream.mapping;

import org.someth2say.taijitu.equality.aspects.external.Hasher;
import org.someth2say.taijitu.stream.StreamEqualizer;
import org.someth2say.taijitu.equality.wrapper.HashableWrapper;
import org.someth2say.taijitu.equality.explain.Difference;
import org.someth2say.taijitu.equality.explain.Missing;
import org.someth2say.taijitu.equality.explain.Unequal;
import org.someth2say.taijitu.stream.StreamUtil;

import java.util.Iterator;
import java.util.Map;
import java.util.Spliterators;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by Jordi Sola on 02/03/2017.
 */
public class HashingStreamEqualizer<T> implements StreamEqualizer<T> {

    final private Hasher<T> hasher;

    public HashingStreamEqualizer(Hasher<T> hasher) {
        this.hasher = hasher;
    }

    @Override
    public Stream<Difference> explain(Stream<T> source, Stream<T> target) {

        final Stream<OrdinalAndComposite<T>> alternattingStream = StreamUtil.zip(
                source.map(t -> new OrdinalAndComposite<>(1, t)),
                target.map(t -> new OrdinalAndComposite<>(2, t)), 1, true);

        Iterator<Difference> differencesAndMissings = new Iterator<>() {
            Map<HashableWrapper<T>, OrdinalAndComposite<T>> map = new ConcurrentHashMap<>();

            // Using zip, so we can alternate both streams, and produce differences even one of them is infinite.
            private Stream<Difference> mappedDifferences = alternattingStream
                    .flatMap(oac -> map(oac, hasher, map));

            private Iterator<Difference> mappedDifferencesIt = mappedDifferences.iterator();

            @Override
            public boolean hasNext() {
                return mappedDifferencesIt.hasNext() || !map.isEmpty();
            }

            @Override
            public Difference next() {
                if (mappedDifferencesIt.hasNext()) {
                    return mappedDifferencesIt.next();
                } else {
                    //pick next available entry in map and return it as a "missing"
                    OrdinalAndComposite<T> oac = map.remove(map.entrySet().iterator().next().getKey());
                    return new Missing<>(hasher, oac.getComposite());
                }
            }
        };

        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(differencesAndMissings, 0), true);
    }

    public static <T> Stream<Difference> map(OrdinalAndComposite<T> thisOaC, Hasher<T> hasher,
                                             Map<HashableWrapper<T>, OrdinalAndComposite<T>> sharedMap) {
        HashableWrapper<T> wrapper = new HashableWrapper<>(thisOaC.getComposite(), hasher);
        OrdinalAndComposite<T> otherOaC = sharedMap.putIfAbsent(wrapper, new OrdinalAndComposite<>(thisOaC.getOrdinal(), thisOaC.getComposite()));
        if (otherOaC != null) {
            // we have a key match ...
            sharedMap.remove(wrapper);
            Stream<Difference> unequal;
            boolean areEquals = hasher.areEquals(thisOaC.getComposite(), otherOaC.getComposite());
            //TODO; This is not ok. What if two elements from the same stream have the same key? This may drive to errors.

            if (!areEquals) {
                if (thisOaC.getOrdinal() < otherOaC.getOrdinal()) {
                    // The simple case do not recurse explanations, only identify differences
                    unequal = Stream.of(new Unequal<>(hasher, thisOaC.getComposite(), otherOaC.getComposite()));
                } else {
                    unequal = Stream.of(new Unequal<>(hasher, otherOaC.getComposite(), thisOaC.getComposite()));
                }
            } else {
                unequal = Stream.empty();
            }

            return unequal;
        }
        return Stream.empty();
    }

}
