package org.someth2say.taijitu.compare.equality.structure;

import org.someth2say.taijitu.compare.equality.value.ValueEquality;
import org.someth2say.taijitu.util.ImmutablePair;
import org.someth2say.taijitu.util.Pair;

import java.util.function.Function;

public class ExtractorsAndEquality<T,Q,Y> extends ImmutablePair<Pair<Function<T, Y>,Function<Q, Y>>, ValueEquality<Y>> {
    public ExtractorsAndEquality(Pair<Function<T, Y>,Function<Q, Y>> left, ValueEquality<Y> right) {
        super(left, right);
    }

    Pair<Function<T, Y>,Function<Q, Y>> getExtractors() {
        return super.getKey();
    }

    ValueEquality<Y> getEquality() {
        return super.getRight();
    }
}
