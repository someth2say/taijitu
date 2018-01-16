package org.someth2say.taijitu.compare.equality.wrapper;

import org.someth2say.taijitu.compare.equality.aspects.external.Comparator;
import org.someth2say.taijitu.compare.equality.aspects.internal.Comparable;

public class ComparableWrapper<WRAPPED,EQ extends Comparator<WRAPPED>>
        extends EqualizableWrapper<WRAPPED, EQ>
        implements Comparable<Wrapper<WRAPPED,?>> {

    public ComparableWrapper(WRAPPED wrapped, EQ equality) {
        super(wrapped, equality);
    }

    @Override
    public int compareTo(Wrapper<WRAPPED, ?> other) {
        return getEquality().compare(getWraped(), other.getWraped());
    }

    public class Factory<FWRAPPED> {
        private final Comparator<FWRAPPED> comparator;

        public Factory(Comparator<FWRAPPED> comparator){
            this.comparator = comparator;
        }

        public EqualizableWrapper<FWRAPPED, Comparator<FWRAPPED>> wrap(FWRAPPED wrapped){
            return new EqualizableWrapper<>(wrapped, comparator);
        }
    }
}
