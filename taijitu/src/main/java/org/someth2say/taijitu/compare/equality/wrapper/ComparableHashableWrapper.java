package org.someth2say.taijitu.compare.equality.wrapper;

import org.someth2say.taijitu.compare.equality.aspects.external.ComparatorHasher;

public class ComparableHashableWrapper<T, EQ extends ComparatorHasher<T>>
        extends EqualizableWrapper<T, EQ>
        implements IComparableHashableWrapper<T,EQ> {

    public ComparableHashableWrapper(T wrapped, EQ equality) {
        super(wrapped, equality);
    }

    // Unluckily, this can not be pushed up to interface, as java.lang.Object methods can not be defaulted.
    @Override
    public int hashCode() {
        return getEquality().hashCode(getWraped());
    }

    @Override
    public int compareTo(IWraper<T, ?> other) {
        return getEquality().compare(getWraped(), other.getWraped());
    }

}
