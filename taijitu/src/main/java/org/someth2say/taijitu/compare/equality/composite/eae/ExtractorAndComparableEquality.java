package org.someth2say.taijitu.compare.equality.composite.eae;

import org.someth2say.taijitu.compare.equality.value.ComparableValueEquality;

import java.util.function.Function;

public class ExtractorAndComparableEquality<T, Y> extends AbstractExtractorHolder<T,Y> {
    private final ComparableValueEquality<Y> valueEquality;

    public ExtractorAndComparableEquality(Function<T, Y> extractor, ComparableValueEquality<Y> valueEquality) {
        super(extractor);
        this.valueEquality = valueEquality;
    }

    public ComparableValueEquality<Y> getEquality() {
        return this.valueEquality;
    }
}
