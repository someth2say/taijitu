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
        boolean equals = equalizer.equals(firstValue, secondValue);
        getLogger().trace("{}<={}=>{} ({}({}))", firstValue, equals ? "=" : "/", secondValue, equalizer.getClass().getSimpleName(), firstValue.getClass().getName());
        return equals;
    }

    @Override
    default Stream<Difference<?>> underlyingDiffs(T t1, T t2) {
        List<Difference<?>> differenceStream = getExtractorsAndEqualities().stream().<Difference<?>>map(eae -> differenceOrNull(t1, t2, eae)).filter(Objects::nonNull).collect(Collectors.toList());
        return differenceStream.isEmpty() ? null : differenceStream.stream();
    }

    default <V> Difference<V> differenceOrNull(T first, T second, ExtractorAndEquality<T, V, ?> eae) {
        Function<T, V> extractors = eae.getExtractor();
        Equalizer<V> equalizer = eae.getEquality();
        V firstValue = extractors.apply(first);
        V secondValue = extractors.apply(second);
        boolean equals = equalizer.equals(firstValue, secondValue);
        getLogger().trace("{}<={}=>{} ({}({}))", firstValue, equals ? "=" : "/", secondValue, equalizer.getClass().getSimpleName(), firstValue.getClass().getName());
        return equals ? null : new Unequal<>(equalizer, firstValue, secondValue);
    }


}
