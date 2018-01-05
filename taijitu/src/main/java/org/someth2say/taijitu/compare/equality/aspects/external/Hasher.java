package org.someth2say.taijitu.compare.equality.aspects.external;

import org.someth2say.taijitu.compare.equality.wrapper.HashableWrapper;
import org.someth2say.taijitu.compare.equality.wrapper.IHashableWraper;

public interface Hasher<T> extends Equalizer<T> {

    int hashCode(T t);

    @Override
	default IHashableWraper<T,? extends Hasher<T>> wrap(T obj) {
        return new HashableWrapper<>(obj, this);
    }

}
