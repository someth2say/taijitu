package org.someth2say.taijitu.compare.equality.impl.partial;

import org.someth2say.taijitu.compare.equality.aspects.external.Hasher;

import java.util.function.Function;

public class IndirectHasher<T,E> extends IndirectEqualizer<T,E> implements IIndirectHasher<T,E> {

    public IndirectHasher(Function<T, E> extractor, Hasher<E> delegate) {
        super(extractor, delegate);
    }

}

interface IIndirectHasher<T, E> extends Hasher<T>, IIndirect<T, E> {

    @Override
    default int hash(T t) {
        return ((Hasher<E>)getDelegate()).hash(getExtractor().apply(t));
    }

}