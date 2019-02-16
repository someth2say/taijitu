package org.someth2say.taijitu.equality.impl.partial;

import org.someth2say.taijitu.equality.aspects.external.Comparator;

import java.util.function.Function;

public class IndirectComparator<T, E> extends IndirectEqualizer<T, E> implements IIndirectComparator<T, E> {

    public IndirectComparator(Function<T, E> extractor, Comparator<E> delegate) {
        super(extractor, delegate);
    }

}

interface IIndirectComparator<T, E> extends Comparator<T>, IIndirect<T, E> {

    @Override
    default int compare(T t1, T t2) {
        return ((Comparator<E>) getDelegate()).compare(getExtractor().apply(t1), getExtractor().apply(t2));
    }

}