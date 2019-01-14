package org.someth2say.taijitu.compare.equality.impl.composite;

import java.util.stream.Stream;

import org.someth2say.taijitu.compare.equality.aspects.external.Comparator;
import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;

public interface ICompositeComparator<T> extends ICompositeEqualizer<T>, Comparator<T> {

    @Override
    default int compare(T first, T second) {
        Stream<ExtractorAndEquality<T, ?, ? extends Equalizer<?>>> extractorsAndEqualities = this.getExtractorsAndEqualities();
        return extractorsAndEqualities.map(eae-> valueCompareTo(first, second, eae)).any(i -> i!=0);

        
        /*for (ExtractorAndEquality eae : this.getExtractorsAndEqualities()) {
            int valueComparison = valueCompareTo(first, second, eae);
            if (valueComparison != 0) {
                return valueComparison;
            }
        }*/
     
}
     
    default <V> int valueCompareTo(T first, T second, ExtractorAndEquality<T, V, Comparator<V>> eae) {
        Function<T, V> extractor = eae.getExtractor();
        Comparator<V> equality = eae.getEquality();
        V firstValue = extractor.apply(first);
        V secondValue = extractor.apply(second);
        return equality.compare(firstValue, secondValue);
    }
}
