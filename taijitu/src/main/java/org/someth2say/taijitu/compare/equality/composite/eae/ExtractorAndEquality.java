package org.someth2say.taijitu.compare.equality.composite.eae;

import org.someth2say.taijitu.compare.equality.value.ValueEquality;

import java.util.function.Function;

public class ExtractorAndEquality<T, Y> {
    private final Function<T, Y> extractor;
    private final ValueEquality<Y> valueEquality;

    public ExtractorAndEquality(Function<T, Y> extractor, ValueEquality<Y> valueEquality) {
        this.extractor = extractor;
        this.valueEquality = valueEquality;
    }

    public Function<T, Y> getExtractor() {
        return this.extractor;
    }

    public ValueEquality<Y> getEquality() {
        return this.valueEquality;
    }
}
