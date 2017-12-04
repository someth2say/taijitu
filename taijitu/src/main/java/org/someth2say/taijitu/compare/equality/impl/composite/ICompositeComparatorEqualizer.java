package org.someth2say.taijitu.compare.equality.impl.composite;

import org.someth2say.taijitu.compare.equality.aspects.external.ComparatorEqualizer;
import org.someth2say.taijitu.compare.equality.aspects.external.ComparatorHasherEqualizer;

import java.util.function.Function;

public interface ICompositeComparatorEqualizer<T> extends ICompositeEqualizer<T>, ComparatorEqualizer<T> {

    @Override
    default int compare(T first, T second) {
        for (ExtractorAndEquality eae : this.getExtractorsAndEqualities()) {
            int valueComparison = valueCompareTo(first, second, eae);
            if (valueComparison != 0) {
                return valueComparison;
            }
        }
        return 0;
    }

    default <V> int valueCompareTo(T first, T second, ExtractorAndEquality<T, V, ComparatorHasherEqualizer<V>> eae) {
        Function<T, V> extractor = eae.getExtractor();
        ComparatorHasherEqualizer<V> equality = eae.getEquality();
        V firstValue = extractor.apply(first);
        V secondValue = extractor.apply(second);
        int compare = equality.compare(firstValue, secondValue);
        getLogger().trace("{}<={}=>{} ({}({}))", firstValue, compare, secondValue, equality.getClass().getSimpleName(), firstValue.getClass().getName());
        return compare;
    }
}
