package org.someth2say.taijitu.compare.equality.composite.eae;

import org.someth2say.taijitu.compare.equality.Equality;

import java.util.function.Function;

public class ExtractorAndEquality<T, Y> extends AbstractExtractorAndEquality<T, Y, Equality<Y>> {

    public ExtractorAndEquality(Function<T, Y> extractor, Equality<Y> equality) {
        super(equality, extractor);
    }


}
