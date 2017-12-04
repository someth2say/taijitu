package org.someth2say.taijitu.compare.equality.aspects.internal;

import org.someth2say.taijitu.compare.equality.aspects.external.ComparatorEquality;

public interface ComparableEqualizable<T,EQ extends ComparatorEquality<T>> extends Equalizable<T,EQ>, org.someth2say.taijitu.compare.equality.aspects.internal.Comparable<T,EQ> {
}
