package org.someth2say.taijitu.equality.impl.delegating;

import org.someth2say.taijitu.equality.aspects.external.Equalizer;

import java.util.function.Function;

public class DelegatingEqualizer<T,R> extends Delegating<T, R> implements IDelegatingEqualizer<T,R> {

    public DelegatingEqualizer(Function<T, R> extractor, Equalizer<R> delegate) {
        super(extractor, delegate);
    }

    @Override
    public String toString() {
        return "*"+getDelegate().toString();
    }
}
interface IDelegatingEqualizer<T,R> extends IDelegating<T, R>, Equalizer<T> {

    @Override
    default boolean areEquals(T equalized1, T equalized2) {
        R lhs = getExtractor().apply(equalized1);
        R rhs = getExtractor().apply(equalized2);
        return getDelegate().areEquals(lhs, rhs);
    }

}