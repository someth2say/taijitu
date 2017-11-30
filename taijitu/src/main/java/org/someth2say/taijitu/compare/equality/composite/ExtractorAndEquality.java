package org.someth2say.taijitu.compare.equality.composite;

import org.someth2say.taijitu.compare.equality.Equality;

import java.util.function.Function;
class ExtractorAndEquality<T, Y, E extends Equality<Y>> {
    private final E equality;
    private final Function<T, Y> extractor;

    public ExtractorAndEquality(Function<T, Y> extractor,E equality) {
        this.equality = equality;
        this.extractor = extractor;
    }

    public E getEquality() {
        return this.equality;
    }

    public Function<T, Y> getExtractor() {
        return this.extractor;
    }

    @Override
    public String toString() {
        return equality.toString();
    }
}
