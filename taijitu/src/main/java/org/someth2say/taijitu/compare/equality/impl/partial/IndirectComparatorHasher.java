package org.someth2say.taijitu.compare.equality.impl.partial;

import org.someth2say.taijitu.compare.equality.aspects.external.ComparatorHasher;
import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;

import java.util.function.Function;

public class IndirectComparatorHasher<T, E> extends IndirectEqualizer<T, E> implements IIndirectComparatorHasher<T, E> {
    public IndirectComparatorHasher(Function<T, E> extractor, Equalizer<E> delegate) {
        super(extractor, delegate);
    }
}

interface IIndirectComparatorHasher<T, E> extends IIndirectHasher<T, E>, IIndirectComparator<T, E>, ComparatorHasher<T> {
}