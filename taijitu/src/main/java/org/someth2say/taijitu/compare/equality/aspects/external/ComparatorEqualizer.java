package org.someth2say.taijitu.compare.equality.aspects.external;

import org.someth2say.taijitu.compare.equality.aspects.internal.ComparableEqualizable;
import org.someth2say.taijitu.compare.equality.wrapper.ComparableEqualityWrapper;

import java.util.Comparator;

public interface ComparatorEqualizer<T> extends Comparator<T>, Equalizer<T> {

    @Override
	default ComparableEqualizable<T> wrap(T obj) {
        return new ComparableEqualityWrapper<>(obj, this);
    }

}
