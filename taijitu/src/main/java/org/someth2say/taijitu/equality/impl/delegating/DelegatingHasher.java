package org.someth2say.taijitu.equality.impl.delegating;

import org.someth2say.taijitu.equality.aspects.external.Hasher;

import java.util.function.Function;

public class DelegatingHasher<T,E> extends DelegatingEqualizer<T,E> implements IDelegatingHasher<T,E> {

    public DelegatingHasher(Function<T, E> extractor, Hasher<? super E> delegate) {
        super(extractor, delegate);
    }

}

interface IDelegatingHasher<T, E> extends Hasher<T>, IDelegating<T, E> {

    @Override
    default int hash(T t) {
        return ((Hasher<? super E>)getDelegate()).hash(getExtractor().apply(t));
    }

}