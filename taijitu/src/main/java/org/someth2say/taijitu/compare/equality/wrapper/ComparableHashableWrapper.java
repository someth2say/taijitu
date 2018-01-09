package org.someth2say.taijitu.compare.equality.wrapper;

import org.someth2say.taijitu.compare.equality.aspects.external.ComparatorHasher;
import org.someth2say.taijitu.compare.equality.aspects.internal.Comparable;
import org.someth2say.taijitu.compare.equality.aspects.internal.Hashable;

public class ComparableHashableWrapper<WRAPPED, EQ extends ComparatorHasher<WRAPPED>>
        extends EqualizableWrapper<WRAPPED, EQ>
        implements Comparable<IWraper<WRAPPED, ?>>, Hashable<IWraper<WRAPPED, ?>>, IWraper<WRAPPED, EQ> {

    public ComparableHashableWrapper(WRAPPED wrapped, EQ equality) {
        super(wrapped, equality);
    }

    // Unluckily, this can not be pushed up to interface, as java.lang.Object methods can not be defaulted.
    @Override
    public int hashCode() {
        return getEquality().hash(getWraped());
    }

    @Override
    public int compareTo(IWraper<WRAPPED, ?> other) {
        return getEquality().compare(getWraped(), other.getWraped());
    }

    public static class Factory<FWRAPPED> {
        private final ComparatorHasher<FWRAPPED> comparatorHasher;

        public Factory(ComparatorHasher<FWRAPPED> comparatorHasher){
            this.comparatorHasher = comparatorHasher;
        }

        public ComparableHashableWrapper<FWRAPPED, ComparatorHasher<FWRAPPED>> wrapp(FWRAPPED wrapped){
            return new ComparableHashableWrapper<>(wrapped, comparatorHasher);
        }
    }
}
