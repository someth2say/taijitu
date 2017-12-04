package org.someth2say.taijitu.compare.equality.aspects.external;

import org.someth2say.taijitu.compare.equality.aspects.internal.HashableEqualizable;
import org.someth2say.taijitu.compare.equality.wrapper.HashableEqualizableWrapper;

public interface HasherEqualizer<T> extends Hasher<T>, Equalizer<T> {

    @Override
	default HashableEqualizable wrap(T obj) {
        return new HashableEqualizableWrapper<>(obj, this);
    }

}
