package org.someth2say.taijitu.compare.equality.aspects.internal;

import org.someth2say.taijitu.compare.equality.aspects.external.Equality;
import org.someth2say.taijitu.compare.equality.wrapper.Wrapper;

public interface Equalizable<T, EQ extends Equality<T>> extends Wrapper<T, EQ> {
    @Override
	boolean equals(Object obj);
}
