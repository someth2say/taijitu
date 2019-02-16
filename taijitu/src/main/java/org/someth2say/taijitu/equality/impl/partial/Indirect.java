package org.someth2say.taijitu.equality.impl.partial;

import org.someth2say.taijitu.equality.aspects.external.Equalizer;

import java.util.function.Function;

abstract class Indirect<T, R> implements IIndirect<T,R> {
    private Function<T, R> extractor;
    private Equalizer<R> delegate;

    Indirect(Function<T, R> extractor, Equalizer<R> delegate) {
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

interface IIndirect<T, E> {
    Function<T, E> getExtractor();

    Equalizer<E> getDelegate();
}

