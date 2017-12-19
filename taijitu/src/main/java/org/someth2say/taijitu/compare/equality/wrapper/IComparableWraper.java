package org.someth2say.taijitu.compare.equality.wrapper;

import org.someth2say.taijitu.compare.equality.aspects.external.Comparator;
import org.someth2say.taijitu.compare.equality.aspects.internal.Comparable;

public interface IComparableWraper<T, EQ extends Comparator<T>> extends IEqualizableWraper<T,EQ>, Comparable<IWraper<T, ?>>{
}
