package org.someth2say.taijitu.compare.equality.aspects.external;

import org.someth2say.taijitu.compare.equality.aspects.internal.CategorizableEqualizable;
import org.someth2say.taijitu.compare.equality.wrapper.CategorizerEqualityWrapper;

public interface HasherEqualizer<T> extends Hasher<T>, Equalizer<T> {

    @Override
	default CategorizableEqualizable<T> wrap(T obj) {
        return new CategorizerEqualityWrapper<>(obj, this);
    }

}
