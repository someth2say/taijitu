package org.someth2say.taijitu.compare.equality.composite.eae;

import org.someth2say.taijitu.compare.equality.Equality;

import java.util.function.Function;

public class AbstractExtractorAndEquality<T, Y, E extends Equality<Y>> {

    private final E equality;
    private final Function<T, Y> extractor;

    public AbstractExtractorAndEquality(E equality, Function<T, Y> extractor) {
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
