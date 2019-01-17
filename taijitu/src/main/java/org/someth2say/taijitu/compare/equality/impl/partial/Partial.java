package org.someth2say.taijitu.compare.equality.impl.partial;

import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;

import java.util.function.Function;

abstract class Partial<T, R> implements IPartial<T,R> {
    private Function<T, R> extractor;
    private Equalizer<R> delegate;

    Partial(Function<T, R> extractor, Equalizer<R> delegate) {
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

// TODO: Find a better name
interface IPartial<T, E> {
    Function<T, E> getExtractor();

    Equalizer<E> getDelegate();
}

