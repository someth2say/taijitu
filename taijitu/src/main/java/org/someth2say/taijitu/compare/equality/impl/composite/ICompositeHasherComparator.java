package org.someth2say.taijitu.compare.equality.impl.composite;

import org.someth2say.taijitu.compare.equality.aspects.external.ComparatorHasher;

public interface ICompositeHasherComparator<T>
		extends ICompositeComparator<T>, ICompositeHasher<T>, ComparatorHasher<T> {
}
