package org.someth2say.taijitu.compare.equality.internal;

import org.someth2say.taijitu.compare.equality.external.CategorizerEquality;
import org.someth2say.taijitu.compare.equality.wrapper.Wrapper;

public interface CategorizableEqualizable<T, EQ extends CategorizerEquality<T>> extends Equalizable<T, EQ>, Categorizable<T, EQ> {
}
