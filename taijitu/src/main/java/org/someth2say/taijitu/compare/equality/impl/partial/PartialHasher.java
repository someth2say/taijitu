package org.someth2say.taijitu.compare.equality.impl.partial;

import org.someth2say.taijitu.compare.equality.aspects.external.Hasher;

import java.util.function.Function;

public class PartialHasher<T,E> extends PartialEqualizer<T,E> implements IPartialHasher<T,E> {

    public PartialHasher(Function<T, E> extractor, Hasher<E> delegate) {
        super(extractor, delegate);
    }

}

interface IPartialHasher<T, E> extends Hasher<T>, IPartial<T, E> {

    @Override
    default int hash(T t) {
        return ((Hasher<E>)getDelegate()).hash(getExtractor().apply(t));
    }

}