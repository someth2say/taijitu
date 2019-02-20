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
import java.util.Optional;
import java.util.Spliterators;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
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

        Map<HashableWrapper<T>, T> map1 = new ConcurrentHashMap<>();
        Map<HashableWrapper<T>, T> map2 = new ConcurrentHashMap<>();

        //TODO: Simplify
        BiFunction<? super T, ? super T, Stream<Difference>> biMapper = (a,b)-> Stream.of(
                map(a, hasher, map1, map2, (ta,tb)->new Unequal<>(hasher, ta, tb)),
                map(b, hasher, map2, map1, (ta,tb)->new Unequal<>(hasher, tb, ta))
        ).flatMap(Function.identity());

        Function<? super T, Stream<Difference>> aTailer = a -> map(a, hasher, map1, map2, (ta,tb)->new Unequal<>(hasher, ta, tb));
        Function<? super T, Stream<Difference>> bTailer = b -> map(b, hasher, map2, map1, (ta,tb)->new Unequal<>(hasher, tb, ta));
        //TODO: Default filter as empty filter
        BiPredicate<? super T, ? super T> filter = (a, b) -> false;

        Stream<Difference> unequals = StreamUtil.biMapTail(source, target, biMapper, aTailer, bTailer, filter).flatMap(Function.identity());
        Stream<Difference> remaining1 = map1.values().stream().map(t->new Missing<>(hasher,t));
        Stream<Difference> remaining2 = map2.values().stream().map(t->new Missing<>(hasher,t));

        return Stream.concat(Stream.concat(unequals, remaining1), remaining2);
//
//
//        alternattingStream.flatMap(oac -> map(oac, hasher, map)).
//
//                Iterator < Difference > differencesAndMissings = new Iterator<>() {
//            Map<HashableWrapper<T>, OrdinalAndComposite<T>> map = new ConcurrentHashMap<>();
//
//            private Iterator<Difference> mappedDifferencesIt = alternattingStream
//                    .flatMap(oac -> map(oac, hasher, map)).iterator();
//
//            @Override
//            public boolean hasNext() {
//                return mappedDifferencesIt.hasNext() || !map.isEmpty();
//            }
//
//            @Override
//            public Difference next() {
//                if (mappedDifferencesIt.hasNext()) {
//                    return mappedDifferencesIt.next();
//                } else {
//                    //pick next available entry in map and return it as a "missing"
//                    OrdinalAndComposite<T> oac = map.remove(map.entrySet().iterator().next().getKey());
//                    return new Missing<>(hasher, oac.getComposite());
//                }
//            }
//        };
//
//        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(differencesAndMissings, 0), true);
    }

    public static <T> Stream<Difference> map(T thisT, Hasher<T> hasher,
                                             Map<HashableWrapper<T>, T> thisMap,
                                             Map<HashableWrapper<T>, T> otherMap,
                                             BiFunction<T,T,Difference<T>> diffMaker) {
        HashableWrapper<T> thisWrapper = new HashableWrapper<>(thisT, hasher);
        T other = otherMap.remove(thisWrapper);
        if (other != null) {
            // we have a key match ...
            Stream<Difference> unequal;
            boolean areEquals = hasher.areEquals(thisT, other);
            if (!areEquals) {
                unequal = Stream.of(diffMaker.apply(thisT, other));
            } else {
                thisMap.put(thisWrapper,thisT);
                unequal = Stream.empty();
            }

            return unequal;
        }
        return Stream.empty();
    }

}
