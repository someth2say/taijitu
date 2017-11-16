package org.someth2say.taijitu.compare.equality.composite.eae;

import org.someth2say.taijitu.compare.equality.ComparableCategorizerEquality;

import java.util.function.Function;

public class ExtractorAndComparableCategorizerEquality<T, Y> extends AbstractExtractorAndEquality<T, Y, ComparableCategorizerEquality<Y>> {

    public ExtractorAndComparableCategorizerEquality(Function<T, Y> extractor, ComparableCategorizerEquality<Y> valueEquality) {
        super(valueEquality, extractor);
    }


}
