package org.someth2say.taijitu.compare.equality.composite;

import org.someth2say.taijitu.compare.equality.external.ComparatorCategorizerEquality;
import org.someth2say.taijitu.compare.equality.external.ComparatorEquality;

import java.util.function.Function;

public interface ICompositeComparator<T> extends ICompositeEquality<T>, ComparatorEquality<T> {


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

    default <V> int valueCompareTo(T first, T second, ExtractorAndEquality<T, V, ComparatorCategorizerEquality<V>> eae) {
        Function<T, V> extractor = eae.getExtractor();
        ComparatorCategorizerEquality<V> equality = eae.getEquality();
        V firstValue = extractor.apply(first);
        V secondValue = extractor.apply(second);
        int compare = equality.compare(firstValue, secondValue);
        getLogger().trace("{}<={}=>{} ({}({}))", firstValue, compare, secondValue, equality.getClass().getSimpleName(), firstValue.getClass().getName());
        return compare;
    }
}
