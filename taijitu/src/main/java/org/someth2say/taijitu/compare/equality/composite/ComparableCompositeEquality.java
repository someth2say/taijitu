package org.someth2say.taijitu.compare.equality.composite;

import org.someth2say.taijitu.compare.equality.composite.eae.AbstractExtractorHolder;
import org.someth2say.taijitu.compare.equality.composite.eae.ExtractorAndComparableEquality;
import org.someth2say.taijitu.compare.equality.composite.wrapper.ComparableCompositeEqualityWrapper;
import org.someth2say.taijitu.compare.equality.value.ComparableValueEquality;
import org.someth2say.taijitu.compare.equality.value.ValueEquality;

import java.util.List;
import java.util.function.Function;

public class ComparableCompositeEquality<T> implements IComparableCompositeEquality<T> {

    private final List<ExtractorAndComparableEquality<T, ?>> extractorsAndEqualities;

    public ComparableCompositeEquality(List<ExtractorAndComparableEquality<T, ?>> extractorsAndEqualities) {
        this.extractorsAndEqualities = extractorsAndEqualities;
    }

    @Override
    public int compareTo(T first, T second) {
        for (ExtractorAndComparableEquality<T, ?> eae : this.extractorsAndEqualities) {
            int valueComparison = valueCompareTo(first, second, eae);
            if (valueComparison!=0){
                return valueComparison;
            }
        }
        return 0;
    }

    private <V> int valueCompareTo(T first, T second, ExtractorAndComparableEquality<T, V> eae) {
        Function<T, V> extractor = eae.getExtractor();
        ComparableValueEquality<V> equality = eae.getEquality();
        V firstValue = extractor.apply(first);
        V secondValue = extractor.apply(second);
        return equality.compare(firstValue, secondValue);
    }


    /**
     * Default equality is ordered given the list of extractors.
     */
    @Override
    public boolean equals(T first, T second) {
        return extractorsAndEqualities.stream().allMatch(eae -> valueEquals(first, second, eae));
    }

    private <V> boolean valueEquals(T first, T second, AbstractExtractorHolder<T, V> eae) {
        Function<T, V> extractors = eae.getExtractor();
        ValueEquality<V> equality = eae.getEquality();
        V firstValue = extractors.apply(first);
        V secondValue = extractors.apply(second);
        return equality.equals(firstValue, secondValue);
    }

    @Override
    public int hashCode(T obj) {
        int result = 1;
        for (AbstractExtractorHolder<T, ?> eae : extractorsAndEqualities) {
            result = 31 * result + valueHashCode(obj, eae);
        }
        return result;

    }

    private <V> int valueHashCode(T obj, AbstractExtractorHolder<T, V> eae) {
        Function<T, V> key = eae.getExtractor();
        V value = key.apply(obj);
        return eae.getEquality().computeHashCode(value);
    }

    @Override
    public ComparableCompositeEqualityWrapper<T> wrap(T obj) {
        return new ComparableCompositeEqualityWrapper<>(obj, this);
    }
}
