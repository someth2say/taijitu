package org.someth2say.taijitu.compare.equality.wrapper;

import org.someth2say.taijitu.compare.equality.aspects.external.Comparator;
import org.someth2say.taijitu.compare.equality.aspects.internal.Comparable;

public class ComparableWrapper<WRAPPED,EQ extends Comparator<WRAPPED>>
        extends EqualizableWrapper<WRAPPED, EQ>
        implements Comparable<IWraper<WRAPPED,?>>, IWraper<WRAPPED,EQ>{

    public ComparableWrapper(WRAPPED wrapped, EQ equality) {
        super(wrapped, equality);
    }

    @Override
    public int compareTo(IWraper<WRAPPED, ?> other) {
        return getEquality().compare(getWraped(), other.getWraped());
    }
}
