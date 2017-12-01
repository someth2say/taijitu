package org.someth2say.taijitu.compare.equality.internal;

import org.someth2say.taijitu.compare.equality.external.Equality;
import org.someth2say.taijitu.compare.equality.wrapper.Wrapper;

public interface Equalizable<T, EQ extends Equality<T>> extends Wrapper<T, EQ> {
    @Override
	boolean equals(Object obj);
}
