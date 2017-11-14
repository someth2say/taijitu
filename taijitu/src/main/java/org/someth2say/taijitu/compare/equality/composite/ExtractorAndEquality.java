package org.someth2say.taijitu.compare.equality.composite;

import org.someth2say.taijitu.compare.equality.value.ValueEquality;
import org.someth2say.taijitu.util.ImmutablePair;

import java.util.function.Function;

public class ExtractorAndEquality<T,Y> extends ImmutablePair<Function<T, Y>, ValueEquality<Y>> {
    public ExtractorAndEquality(Function<T, Y> left, ValueEquality<Y> right) {
        super(left, right);
    }

    Function<T, Y> getExtractor() {
        return super.getKey();
    }

    ValueEquality<Y> getEquality() {
        return super.getRight();
    }
}
