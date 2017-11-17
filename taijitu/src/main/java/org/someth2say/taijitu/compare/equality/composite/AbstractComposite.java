package org.someth2say.taijitu.compare.equality.composite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.compare.equality.Equality;
import org.someth2say.taijitu.compare.equality.composite.eae.AbstractExtractorAndEquality;

import java.util.List;
import java.util.function.Function;

public abstract class AbstractComposite<T,EAE extends AbstractExtractorAndEquality<T, ?, ?>> implements Composite<T>{
    private static final Logger logger = LoggerFactory.getLogger(AbstractComposite.class);

    private final List<EAE> extractorsAndEqualities;

    protected AbstractComposite(List<EAE> extractorsAndEqualities) {
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


    public List<EAE> getExtractorsAndEqualities() {
        return extractorsAndEqualities;
    }

}