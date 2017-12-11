package org.someth2say.taijitu.compare.equality.aspects.external;

import org.someth2say.taijitu.compare.equality.wrapper.ComparableWrapper;
import org.someth2say.taijitu.compare.equality.wrapper.IComparableWraper;

public interface Comparator<T> extends java.util.Comparator<T>, Equalizer<T> {

    @Override
    default IComparableWraper<T, ?> wrap(T obj) {
        return new ComparableWrapper<>(obj, this);
    }

}
