package org.someth2say.taijitu.compare.equality.impl.composite;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.compare.result.Difference;
import org.someth2say.taijitu.compare.result.Unequal;

public interface ICompositeEqualizer<T> extends IComposite<T>, Equalizer<T> {

    @Override
    default boolean areEquals(T first, T second) {
        Stream<ExtractorAndEquality<T, ?, ? extends Equalizer<?>>> extractorsAndEqualities = getExtractorsAndEqualities();
        return extractorsAndEqualities.allMatch(eae -> valueEquals(first, second, eae));
    }

    default <V> boolean valueEquals(T first, T second, ExtractorAndEquality<T, V, ? extends Equalizer<? super V>> eae) {
        Function<T, V> extractors = eae.getExtractor();
        Equalizer<? super V> equalizer = eae.getEquality();
        V firstValue = extractors.apply(first);
        V secondValue = extractors.apply(second);
        return equalizer.areEquals(firstValue, secondValue);
    }

    // TODO: This is actually a piece of shit!!!
    // Need to work out to the iterator not to be consumed (but then, how filter out
    // non-different elements?)
    @Override
    default Stream<Difference<?>> underlyingDiffs(T t1, T t2) {
        Stream<ExtractorAndEquality<T, ?, ? extends Equalizer<?>>> extractorsAndEqualities = getExtractorsAndEqualities();
        Stream<Difference<?>> differenceStream = extractorsAndEqualities
                .<Difference<?>>map(eae -> differenceOrNull(t1, t2, eae)).filter(Objects::nonNull);
        return differenceStream;
    }

    default <V> Difference<V> differenceOrNull(T first, T second, ExtractorAndEquality<T, V, ?> eae) {
        Function<T, V> extractors = eae.getExtractor();
        Equalizer<? super V> equalizer = eae.getEquality();
        V firstValue = extractors.apply(first);
        V secondValue = extractors.apply(second);
        boolean equals = equalizer.areEquals(firstValue, secondValue);
        return equals ? null : new Unequal<>(equalizer, firstValue, secondValue);
    }

}
