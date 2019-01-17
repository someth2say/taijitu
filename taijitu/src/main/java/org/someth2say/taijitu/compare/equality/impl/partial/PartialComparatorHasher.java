package org.someth2say.taijitu.compare.equality.impl.partial;

import org.someth2say.taijitu.compare.equality.aspects.external.ComparatorHasher;
import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;

import java.util.function.Function;

public class PartialComparatorHasher<T, E> extends PartialEqualizer<T, E> implements IPartialComparatorHasher<T, E> {
    public PartialComparatorHasher(Function<T, E> extractor, Equalizer<E> delegate) {
        super(extractor, delegate);
    }
}

interface IPartialComparatorHasher<T, E> extends IPartialHasher<T, E>, IPartialComparator<T, E>, ComparatorHasher<T> {
}