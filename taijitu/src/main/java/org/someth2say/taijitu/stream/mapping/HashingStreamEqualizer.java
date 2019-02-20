package org.someth2say.taijitu.stream.mapping;

import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.someth2say.taijitu.equality.aspects.external.Hasher;
import org.someth2say.taijitu.equality.explain.Difference;
import org.someth2say.taijitu.equality.explain.Missing;
import org.someth2say.taijitu.equality.explain.Unequal;
import org.someth2say.taijitu.equality.wrapper.HashableWrapper;
import org.someth2say.taijitu.stream.StreamEqualizer;
import org.someth2say.taijitu.stream.StreamUtil;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Stream;

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

        ArrayListValuedHashMap<HashableWrapper<T>, T> map1 = new ArrayListValuedHashMap<>();
        ArrayListValuedHashMap<HashableWrapper<T>, T> map2 = new ArrayListValuedHashMap<>();

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

        Stream<Difference> remaining1 = Stream.generate(()->{
            if (map1.isEmpty()) return null;
            Map.Entry<HashableWrapper<T>, T> next = map1.entries().iterator().next();
            map1.removeMapping(next.getKey(),next.getValue());
            return (Difference)new Missing<>(hasher,next.getValue());
        }).takeWhile(Objects::nonNull);

        Stream<Difference> remaining2 = Stream.generate(()->{
            if (map2.isEmpty()) return null;
            Map.Entry<HashableWrapper<T>, T> next = map2.entries().iterator().next();
            map2.removeMapping(next.getKey(),next.getValue());
            return (Difference)new Missing<>(hasher,next.getValue());
        }).takeWhile(Objects::nonNull);

        return Stream.concat(Stream.concat(unequals, remaining1), remaining2);
    }

    public static <T> Stream<Difference> map(T thisT, Hasher<T> hasher,
                                             ArrayListValuedHashMap<HashableWrapper<T>, T> thisMap,
                                             ArrayListValuedHashMap<HashableWrapper<T>, T> otherMap,
                                             BiFunction<T,T,Difference<T>> diffMaker) {

        HashableWrapper<T> thisWrapper = new HashableWrapper<>(thisT, hasher);
        //TODO: Investigate efficiency
        synchronized (hasher) {
            if (otherMap.containsKey(thisWrapper)) {
                T other = otherMap.get(thisWrapper).get(0);
                otherMap.removeMapping(thisWrapper,other);
                // we have a key match ...
                if (!hasher.areEquals(thisT, other)) {
                    Difference<T> apply = diffMaker.apply(thisT, other);
                    return Stream.of(apply);
                } else {
                    return Stream.empty();
                }
            } else {
                thisMap.put(thisWrapper, thisT);
                return Stream.empty();
            }
        }

    }

}
