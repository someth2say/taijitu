package org.someth2say.taijitu.equality.impl.delegating;

import org.someth2say.taijitu.equality.aspects.external.ComparatorHasher;
import org.someth2say.taijitu.equality.aspects.external.Equalizer;

import java.util.function.Function;

public class DelegatingComparatorHasher<T, E> extends DelegatingEqualizer<T, E> implements IDelegatingComparatorHasher<T, E> {
    public DelegatingComparatorHasher(Function<T, E> extractor, Equalizer<E> delegate) {
        super(extractor, delegate);
    }
}

interface IDelegatingComparatorHasher<T, E> extends IDelegatingHasher<T, E>, IDelegatingComparator<T, E>, ComparatorHasher<T> {
}