package org.someth2say.taijitu.compare.equality.impl.stream.mapping;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.someth2say.taijitu.compare.equality.aspects.external.Hasher;
import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.compare.equality.impl.stream.StreamEqualizer;
import org.someth2say.taijitu.compare.equality.wrapper.IHashableWraper;
import org.someth2say.taijitu.compare.result.Difference;
import org.someth2say.taijitu.compare.result.Unequal;
import org.someth2say.taijitu.util.StreamUtil;

/**
 * Created by Jordi Sola on 02/03/2017.
 */
public class HashingStreamEqualizer<T> implements StreamEqualizer<T> {

    final private Equalizer<T> equalizer;
    final private Hasher<T> hasher;


    public HashingStreamEqualizer(Equalizer<T> equalizer, Hasher<T> hasher) {
        this.equalizer = equalizer;
        this.hasher = hasher;
    }

    @Override
    public Stream<Difference<?>> underlyingDiffs(Stream<T> source, Stream<T> target) {


        Iterator<Difference<?>> it = new Iterator<Difference<?>>() {
            Map<IHashableWraper<T, ?>, OrdinalAndComposite<T>> sharedMap = new ConcurrentHashMap<>();

            // Using zip, so we can alternate both streams, and produce differences even one of them is infinite.
            private Stream<Unequal<T>> stream = StreamUtil.zip(source.map(t -> new OrdinalAndComposite<>(1, t)), target.map(t -> new OrdinalAndComposite<>(2, t)), 1, true)
                    .map(oac -> Mapper.map(oac, hasher, sharedMap, equalizer)).filter(Objects::nonNull);

            private Iterator<Unequal<T>> unequals = stream.iterator();

            @Override
            public boolean hasNext() {
                // This 'unequals.hasNext' can be infinite!!
                return unequals.hasNext() || !sharedMap.isEmpty();
            }

            @Override
            public Difference<?> next() {
                if (unequals.hasNext()) {
                    return unequals.next();
                } else {
                    OrdinalAndComposite<T> oac = sharedMap.remove(sharedMap.entrySet().iterator().next().getKey());
                    return hasher.asMissing(oac.getComposite());
                }
            }
        };

        // We are here creating a non-parallel stream! :(
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(it, 0), false);
    }

}
