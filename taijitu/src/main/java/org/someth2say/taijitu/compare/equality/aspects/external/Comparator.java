package org.someth2say.taijitu.compare.equality.aspects.external;

import org.someth2say.taijitu.compare.equality.aspects.internal.Comparable;
import org.someth2say.taijitu.compare.equality.wrapper.ComparableWrapper;

public interface Comparator<T> extends java.util.Comparator<T>, Equalizer<T> {

    @Override
	default Comparable<T> wrap(T obj) {
        return new ComparableWrapper<>(obj, this);
    }

}
