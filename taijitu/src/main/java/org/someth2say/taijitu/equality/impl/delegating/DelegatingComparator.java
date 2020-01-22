package org.someth2say.taijitu.equality.impl.delegating;

import org.someth2say.taijitu.equality.aspects.external.Comparator;

import java.util.function.Function;

public class DelegatingComparator<T, E> extends DelegatingEqualizer<T, E> implements IDelegatingComparator<T, E> {
    public DelegatingComparator(Function<T, E> extractor, Comparator<? super E> delegate) {
        super(extractor, delegate);
    }
}

interface IDelegatingComparator<T, E> extends Comparator<T>, IDelegating<T, E> {
    @Override
    default int compare(T t1, T t2) {
        return ((Comparator<? super E>) getDelegate()).compare(getExtractor().apply(t1), getExtractor().apply(t2));
    }
}