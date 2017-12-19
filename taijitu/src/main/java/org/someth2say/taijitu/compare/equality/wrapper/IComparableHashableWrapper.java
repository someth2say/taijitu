package org.someth2say.taijitu.compare.equality.wrapper;

import org.someth2say.taijitu.compare.equality.aspects.external.ComparatorHasher;

public interface IComparableHashableWrapper<T, EQ extends ComparatorHasher<T>> extends IHashableWraper<T, EQ>, IComparableWraper<T, EQ> {
}
