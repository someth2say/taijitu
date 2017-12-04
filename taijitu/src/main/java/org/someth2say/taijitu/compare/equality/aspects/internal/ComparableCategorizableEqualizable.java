package org.someth2say.taijitu.compare.equality.aspects.internal;

import org.someth2say.taijitu.compare.equality.aspects.external.ComparatorCategorizerEquality;

public interface ComparableCategorizableEqualizable<T, EQ extends ComparatorCategorizerEquality<T>>
        extends ComparableEqualizable<T, EQ>, CategorizableEqualizable<T, EQ> {
}
