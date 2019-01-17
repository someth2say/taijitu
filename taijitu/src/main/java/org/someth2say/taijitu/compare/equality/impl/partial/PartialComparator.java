package org.someth2say.taijitu.compare.equality.impl.partial;

import org.someth2say.taijitu.compare.equality.aspects.external.Comparator;

import java.util.function.Function;

public class PartialComparator<T, E> extends PartialEqualizer<T, E> implements IPartialComparator<T, E> {

    public PartialComparator(Function<T, E> extractor, Comparator<E> delegate) {
        super(extractor, delegate);
    }

}

interface IPartialComparator<T, E> extends Comparator<T>, IPartial<T, E> {

    @Override
    default int compare(T t1, T t2) {
        return ((Comparator<E>) getDelegate()).compare(getExtractor().apply(t1), getExtractor().apply(t2));
    }

}