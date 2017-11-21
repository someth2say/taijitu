package org.someth2say.taijitu.compare.equality.composite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.compare.equality.Equality;
import org.someth2say.taijitu.compare.equality.composite.eae.AbstractExtractorAndEquality;
import org.someth2say.taijitu.compare.result.Difference;
import org.someth2say.taijitu.compare.result.Mismatch;

import java.util.List;
import java.util.function.Function;

public abstract class AbstractCompositeEquality<T, EAE extends AbstractExtractorAndEquality<T, ?, ?>> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractCompositeEquality.class);

    private final List<EAE> extractorsAndEqualities;

    protected AbstractCompositeEquality(List<EAE> extractorsAndEqualities) {
        this.extractorsAndEqualities = extractorsAndEqualities;
    }

    protected <V> boolean valueEquals(T first, T second, AbstractExtractorAndEquality<T, V, ?> eae) {
        Function<T, V> extractors = eae.getExtractor();
        Equality<V> equality = eae.getEquality();
        V firstValue = extractors.apply(first);
        V secondValue = extractors.apply(second);
        boolean equals = equality.equals(firstValue, secondValue);
        logger.trace("{}<={}=>{} ({}({}))", firstValue, equals ? "=" : "/", secondValue, equality.getClass().getSimpleName(), firstValue.getClass().getName());
        return equals;
    }

    protected <T,V> Mismatch difference(T first, T second, AbstractExtractorAndEquality<T, V, ?> eae) {
        Function<T, V> extractors = eae.getExtractor();
        Equality<V> equality = eae.getEquality();
        V firstValue = extractors.apply(first);
        V secondValue = extractors.apply(second);
        boolean equals = equality.equals(firstValue, secondValue);
        logger.trace("{}<={}=>{} ({}({}))", firstValue, equals ? "=" : "/", secondValue, equality.getClass().getSimpleName(), firstValue.getClass().getName());
        //TODO: Get rid of null ids
        return equals ? null : new Difference<>(equality, "1", firstValue, "2", secondValue);
    }


    public List<EAE> getExtractorsAndEqualities() {
        return extractorsAndEqualities;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}