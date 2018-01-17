package org.someth2say.taijitu.compare.equality.wrapper;

import org.someth2say.taijitu.compare.equality.aspects.external.Comparator;
import org.someth2say.taijitu.compare.equality.aspects.internal.Comparable;

public class ComparableWrapper<WRAPPED>
        extends EqualizableWrapper<WRAPPED>
        implements Comparable<Wrapper<WRAPPED>> {

    public Comparator<WRAPPED> getComparator() {
        return comparator;
    }

    private final Comparator<WRAPPED> comparator;

    public ComparableWrapper(WRAPPED wrapped, Comparator<WRAPPED> comparator) {
        super(wrapped, comparator);
        this.comparator = comparator;
    }

    @Override
    public int compareTo(Wrapper<WRAPPED> other) {
        return getComparator().compare(getWraped(), other.getWraped());
    }

    public static class Factory<FWRAPPED> implements Wrapper.Factory<FWRAPPED> {
        private final Comparator<FWRAPPED> comparator;

        public Factory(Comparator<FWRAPPED> comparator) {
            this.comparator = comparator;
        }

        @Override
        public ComparableWrapper<FWRAPPED> wrap(FWRAPPED wrapped) {
            return new ComparableWrapper<>(wrapped, comparator);
        }

    }
}
