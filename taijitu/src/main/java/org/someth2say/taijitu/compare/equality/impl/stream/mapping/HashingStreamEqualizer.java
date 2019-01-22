package org.someth2say.taijitu.compare.equality.impl.stream.mapping;

import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.compare.equality.aspects.external.Hasher;
import org.someth2say.taijitu.compare.equality.impl.stream.StreamEqualizer;
import org.someth2say.taijitu.compare.equality.wrapper.HashableWrapper;
import org.someth2say.taijitu.compare.result.Difference;
import org.someth2say.taijitu.compare.result.Missing;
import org.someth2say.taijitu.util.StreamUtil;

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

    final private Equalizer<T> equalizer;
    final private Hasher<T> hasher;

    public HashingStreamEqualizer(Hasher<T> hasher) {
        this.equalizer = hasher;
        this.hasher = hasher;
    }

    public HashingStreamEqualizer(Equalizer<T> equalizer, Hasher<T> hasher) {
        this.equalizer = equalizer;
        this.hasher = hasher;
    }

    @Override
    public Stream<Difference> explain(Stream<T> source, Stream<T> target) {
        Iterator<Difference> differencesAndMissings = new Iterator<>() {
            Map<HashableWrapper<T>, OrdinalAndComposite<T>> map = new ConcurrentHashMap<>();

            // Using zip, so we can alternate both streams, and produce differences even one of them is infinite.
            private Stream<Difference> mappedDifferences = StreamUtil.zip(
                    source.map(t -> new OrdinalAndComposite<>(1, t)),
                    target.map(t -> new OrdinalAndComposite<>(2, t)), 1, true)
                    .flatMap(oac -> Mapper.map(oac, hasher, map, equalizer));

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
                    return new Missing<>(hasher,oac.getComposite());
                }
            }
        };

        // TODO: We are here creating a non-parallel stream! :(
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(differencesAndMissings, 0), false);
    }

}
