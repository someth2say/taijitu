package org.someth2say.taijitu.equality.impl.delegating;

import org.someth2say.taijitu.equality.aspects.external.Equalizer;

import java.util.function.Function;

abstract class Delegating<T, R> implements IDelegating<T,R> {
    private Function<T, R> extractor;
    private Equalizer<R> delegate;

    Delegating(Function<T, R> extractor, Equalizer<R> delegate) {
        this.extractor = extractor;
        this.delegate = delegate;
    }

    public Function<T, R> getExtractor() {
        return extractor;
    }

    public Equalizer<R> getDelegate() {
        return delegate;
    }

}

interface IDelegating<T, E> {
    Function<T, E> getExtractor();

    Equalizer<E> getDelegate();
}

