package org.someth2say.taijitu.compare.equality.aspects.external;

import org.someth2say.taijitu.compare.equality.aspects.internal.Hashable;
import org.someth2say.taijitu.compare.equality.wrapper.HashableWrapper;

public interface Hasher<T> extends Equalizer<T> {

    int hashCode(T t);

    @Override
	default Hashable<T> wrap(T obj) {
        return new HashableWrapper<>(obj, this);
    }

}
