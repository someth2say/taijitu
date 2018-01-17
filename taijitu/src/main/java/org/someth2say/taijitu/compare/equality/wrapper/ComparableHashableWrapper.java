package org.someth2say.taijitu.compare.equality.wrapper;

import org.someth2say.taijitu.compare.equality.aspects.external.ComparatorHasher;
import org.someth2say.taijitu.compare.equality.aspects.internal.Comparable;
import org.someth2say.taijitu.compare.equality.aspects.internal.Hashable;

public class ComparableHashableWrapper<WRAPPED>
        extends EqualizableWrapper<WRAPPED>
        implements Comparable<Wrapper<WRAPPED>>, Hashable<Wrapper<WRAPPED>> {

    public ComparatorHasher<WRAPPED> getComparatorHasher() {
        return comparatorHasher;
    }

    private final ComparatorHasher<WRAPPED> comparatorHasher;

    public ComparableHashableWrapper(WRAPPED wrapped, ComparatorHasher<WRAPPED> comparatorHasher) {
        super(wrapped, comparatorHasher);
        this.comparatorHasher = comparatorHasher;
    }

    // Unluckily, this can not be pushed up to interface, as java.lang.Object methods can not be defaulted.
    @Override
    public int hashCode() {
        return getComparatorHasher().hash(getWraped());
    }

    @Override
    public int compareTo(Wrapper<WRAPPED> other) {
        return getComparatorHasher().compare(getWraped(), other.getWraped());
    }

    public static class Factory<FWRAPPED> implements Wrapper.Factory<ComparableHashableWrapper<FWRAPPED>, FWRAPPED> {
        private final ComparatorHasher<FWRAPPED> comparatorHasher;

        public Factory(ComparatorHasher<FWRAPPED> comparatorHasher) {
            this.comparatorHasher = comparatorHasher;
        }

        @Override
        public ComparableHashableWrapper<FWRAPPED> wrap(FWRAPPED wrapped) {
            return new ComparableHashableWrapper<>(wrapped, comparatorHasher);
        }
    }
}
