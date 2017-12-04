package org.someth2say.taijitu.compare.equality.aspects.internal;

import org.someth2say.taijitu.compare.equality.aspects.external.CategorizerEquality;
import org.someth2say.taijitu.compare.equality.wrapper.Wrapper;

public interface Categorizable<T, EQ extends CategorizerEquality<T>> extends Wrapper<T, EQ> {
    @Override
	int hashCode();
}
