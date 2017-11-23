package org.someth2say.taijitu.compare.equality.composite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.compare.equality.ComparableCategorizerEquality;
import org.someth2say.taijitu.compare.equality.composite.eae.ExtractorAndComparableCategorizerEquality;
import org.someth2say.taijitu.compare.result.Mismatch;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CompositeComparableCategorizerEquality<T> extends AbstractCompositeEquality<T, ExtractorAndComparableCategorizerEquality<T, ?>> implements ComparableCategorizerEquality<T> {
    private static final Logger logger = LoggerFactory.getLogger(CompositeComparableCategorizerEquality.class);

    public CompositeComparableCategorizerEquality(List<ExtractorAndComparableCategorizerEquality<T, ?>> extractorsAndEqualities) {
        super(extractorsAndEqualities);
    }

    @Override
    public int compare(T first, T second) {
        for (ExtractorAndComparableCategorizerEquality<T, ?> eae : this.getExtractorsAndEqualities()) {
            int valueComparison = valueCompareTo(first, second, eae);
            if (valueComparison != 0) {
                return valueComparison;
            }
        }
        return 0;
    }

    private <V> int valueCompareTo(T first, T second, ExtractorAndComparableCategorizerEquality<T, V> eae) {
        Function<T, V> extractor = eae.getExtractor();
        ComparableCategorizerEquality<V> equality = eae.getEquality();
        V firstValue = extractor.apply(first);
        V secondValue = extractor.apply(second);
        int compare = equality.compare(firstValue, secondValue);
        logger.trace("{}<={}=>{} ({}({}))", firstValue, compare, secondValue, equality.getClass().getSimpleName(), firstValue.getClass().getName());
        return compare;
    }

    /**
     * Default equality is ordered given the list of extractors.
     */
    @Override
    public boolean equals(T first, T second) {
        return getExtractorsAndEqualities().stream().allMatch(eae -> valueEquals(first, second, eae));
    }

    @Override
    public List<Mismatch> differences(T t1, T t2) {
        return getExtractorsAndEqualities().stream().map(eae -> difference(t1, t2, eae)).collect(Collectors.toList());
    }

    @Override
    public int hashCode(T obj) {
        int result = 1;
        for (ExtractorAndComparableCategorizerEquality<T, ?> eae : getExtractorsAndEqualities()) {
            result = 31 * result + valueHashCode(obj, eae);
        }
        return result;
    }

    private <V> int valueHashCode(T obj, ExtractorAndComparableCategorizerEquality<T, V> eae) {
        Function<T, V> key = eae.getExtractor();
        V value = key.apply(obj);
        return eae.getEquality().hashCode(value);
    }

}
