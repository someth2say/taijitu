package org.someth2say.taijitu.compare.equality.composite;

import org.someth2say.taijitu.compare.equality.value.ComparableValueEquality;

import java.util.List;
import java.util.function.Function;

public class ComparableCompositeEquality<T> extends CompositeEquality<T> implements IComparableCompositeEquality<T> {

    public ComparableCompositeEquality(List<ExtractorAndEquality<T, ?>> extractorsAndEqualities) {
        super(extractorsAndEqualities);
    }

    @Override
    public int compareTo(T first, T second) {
        for (ExtractorAndEquality<T, ?> eae : this.extractorsAndEqualities) {
            int valueComparison = valueCompareTo(first, second, eae);
            if (valueComparison!=0){
                return valueComparison;
            }
        }
        return 0;
    }

    private <V> int valueCompareTo(T first, T second, ExtractorAndEquality<T, V> eae) {
        Function<T, V> extractor = eae.getExtractor();
        //TODO Cast! Nooooo.....
        ComparableValueEquality<V> equality = (ComparableValueEquality<V>) eae.getEquality();
        V firstValue = extractor.apply(first);
        V secondValue = extractor.apply(second);
        return equality.compare(firstValue, secondValue);
    }

    @Override
    public ComparableCompositeEqualityWrapper<T> wrap(T obj) {
        return new ComparableCompositeEqualityWrapper<>(obj, this);
    }
}
