package org.someth2say.taijitu.compare.equality.composite.eae;

import org.someth2say.taijitu.compare.equality.value.ValueEquality;

import java.util.function.Function;

public abstract class AbstractExtractorHolder<T, V> {
    private Function<T, V> extractor;

    public AbstractExtractorHolder(Function<T, V> extractor) {
        this.extractor = extractor;
    }

    public Function<T, V> getExtractor() {
        return extractor;
    }

    public abstract ValueEquality<V> getEquality();
}
