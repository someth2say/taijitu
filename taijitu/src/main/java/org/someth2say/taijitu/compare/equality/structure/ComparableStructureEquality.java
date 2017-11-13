package org.someth2say.taijitu.compare.equality.structure;

import org.someth2say.taijitu.compare.equality.value.ComparableValueEquality;

import java.util.List;
import java.util.function.Function;

public class ComparableStructureEquality<T> extends StructureEquality<T> implements IComparableStructureEquality<T> {

    protected ComparableStructureEquality(List<ExtractorsAndEquality<T, ?>> extractorsAndEqualities) {
        super(extractorsAndEqualities);
    }

    @Override
    public int compareTo(T first, T second) {
        for (ExtractorsAndEquality<T, ?> eae : this.extractorsAndEqualities) {
            int valueComparison = valueCompareTo(first, second, eae);
            if (valueComparison!=0){
                return valueComparison;
            }
        }
        return 0;
    }

    private <V> int valueCompareTo(T first, T second, ExtractorsAndEquality<T, V> eae) {
        Function<T, V> extractor = eae.getExtractors();
        //TODO Cast! Nooooo.....
        ComparableValueEquality<V> equality = (ComparableValueEquality<V>) eae.getEquality();
        V firstValue = extractor.apply(first);
        V secondValue = extractor.apply(second);
        return equality.compare(firstValue, secondValue);
    }

    @Override
    public ComparableStructureEqualityWrapper<T> wrap(T obj) {
        return new ComparableStructureEqualityWrapper<>(obj, this);
    }
}
