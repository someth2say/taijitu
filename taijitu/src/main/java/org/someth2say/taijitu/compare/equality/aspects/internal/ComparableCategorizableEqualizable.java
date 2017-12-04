package org.someth2say.taijitu.compare.equality.internal;

import org.someth2say.taijitu.compare.equality.external.ComparatorCategorizerEquality;

public interface ComparableCategorizableEqualizable<T, EQ extends ComparatorCategorizerEquality<T>>
        extends ComparableEqualizable<T, EQ>, CategorizableEqualizable<T, EQ> {
}
