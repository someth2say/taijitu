package org.someth2say.taijitu.compare.equality.impl.composite;

import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.compare.result.Difference;
import org.someth2say.taijitu.compare.result.Unequal;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ICompositeEqualizer<T> extends IComposite, Equalizer<T> {

    @Override
    default boolean equals(T first, T second) {
        return getExtractorsAndEqualities().stream().allMatch(eae -> valueEquals(first, second, eae));
    }

    default <V> boolean valueEquals(T first, T second, ExtractorAndEquality<T, V, ?> eae) {
        Function<T, V> extractors = eae.getExtractor();
        Equalizer<V> equalizer = eae.getEquality();
        V firstValue = extractors.apply(first);
        V secondValue = extractors.apply(second);
        return equalizer.equals(firstValue, secondValue);
    }

    //TODO: This is actually a piece of shit!!! Need to work out to the iterator not to be consumed (but then, how filter out non-different elements?)
    @Override
    default Stream<Difference<?>> underlyingDiffs(T t1, T t2) {
        Stream<Difference<?>> differenceStream = getExtractorsAndEqualities().stream().<Difference<?>>map(eae -> differenceOrNull(t1, t2, eae)).filter(Objects::nonNull);
        List<Difference<?>> differences = differenceStream.collect(Collectors.toList());
        return differences.isEmpty() ? null : differences.stream();
    }

    default <V> Difference<V> differenceOrNull(T first, T second, ExtractorAndEquality<T, V, ?> eae) {
        Function<T, V> extractors = eae.getExtractor();
        Equalizer<V> equalizer = eae.getEquality();
        V firstValue = extractors.apply(first);
        V secondValue = extractors.apply(second);
        boolean equals = equalizer.equals(firstValue, secondValue);
        return equals ? null : new Unequal<>(equalizer, firstValue, secondValue);
    }


}
