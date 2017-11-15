package org.someth2say.taijitu.compare.equality.composite;

import org.someth2say.taijitu.compare.equality.composite.eae.AbstractExtractorHolder;
import org.someth2say.taijitu.compare.equality.composite.eae.ExtractorAndEquality;
import org.someth2say.taijitu.compare.equality.composite.wrapper.CompositeEqualityWrapper;
import org.someth2say.taijitu.compare.equality.value.ValueEquality;

import java.util.List;
import java.util.function.Function;

/**
 * This is the simplest class of CompositeEquality. It assumes that:
 * <OL><LI>Both sides of equality are the same type and</LI>
 * <li>Both sides of equality are using the same extractors</li></OL>
 *
 * @param <T>
 */
public class CompositeEquality<T> implements ICompositeEquality<T> {

    final List<ExtractorAndEquality<T, ?>> extractorsAndEqualities;

    public CompositeEquality(List<ExtractorAndEquality<T, ?>> extractorsAndEqualities) {
        this.extractorsAndEqualities = extractorsAndEqualities;
    }

    /**
     * Default equality is ordered given the list of extractors.
     */
    @Override
    public boolean equals(T first, T second) {
        return extractorsAndEqualities.stream().allMatch(eae -> valueEquals(first, second, eae));
    }

    private <V> boolean valueEquals(T first, T second, ExtractorAndEquality<T, V> eae) {
        Function<T, V> extractors = eae.getExtractor();
        ValueEquality<V> equality = eae.getEquality();
        V firstValue = extractors.apply(first);
        V secondValue = extractors.apply(second);
        return equality.equals(firstValue, secondValue);
    }

    @Override
    public int hashCode(T obj) {
        int result = 1;
        for (ExtractorAndEquality<T, ?> eae : extractorsAndEqualities) {
            result = 31 * result + valueHashCode(obj, eae);
        }
        return result;

    }

    private <V> int valueHashCode(T obj, ExtractorAndEquality<T, V> eae) {
        Function<T, V> key = eae.getExtractor();
        V value = key.apply(obj);
        return eae.getEquality().computeHashCode(value);
    }

    @Override
    public CompositeEqualityWrapper<T> wrap(T obj) {
        return new CompositeEqualityWrapper<>(obj, this);
    }
}
