package org.someth2say.taijitu.compare.equality.impl.composite;

import org.someth2say.taijitu.compare.equality.aspects.external.ComparatorHasherEqualizer;

public interface ICompositeHasherComparatorEqualizer<T>
		extends ICompositeComparatorEqualizer<T>, ICompositeHasherEqualizer<T>, ComparatorHasherEqualizer<T> {
}
