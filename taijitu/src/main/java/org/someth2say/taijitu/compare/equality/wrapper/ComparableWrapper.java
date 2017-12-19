package org.someth2say.taijitu.compare.equality.wrapper;

import org.someth2say.taijitu.compare.equality.aspects.external.Comparator;

public class ComparableWrapper<T,EQ extends Comparator<T>>
        extends EqualizableWrapper<T, EQ>
        implements IComparableWraper<T,EQ> {

    public ComparableWrapper(T wrapped, EQ equality) {
        super(wrapped, equality);
    }

    @Override
    public int compareTo(IWraper<T, ?> other) {
        return getEquality().compare(getWraped(), other.getWraped());
    }
}
