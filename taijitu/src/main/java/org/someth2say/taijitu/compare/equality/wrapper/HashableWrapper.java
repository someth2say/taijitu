package org.someth2say.taijitu.compare.equality.wrapper;

import org.someth2say.taijitu.compare.equality.aspects.external.Hasher;
import org.someth2say.taijitu.compare.equality.aspects.internal.Hashable;

public class HashableWrapper<WRAPPED>
        extends EqualizableWrapper<WRAPPED, Hasher<WRAPPED>>
        implements Hashable<Wrapper<WRAPPED,?>> {

    public HashableWrapper(WRAPPED wrapped, Hasher<WRAPPED> hasher) {
        super(wrapped, hasher);
    }

    @Override
    public int hashCode() {
        return getEquality().hash(getWraped());
    }

    public class Factory<FWRAPPED> {
        private final Hasher<FWRAPPED> hasher;

        public Factory(Hasher<FWRAPPED> hasher){
            this.hasher = hasher;
        }

        public HashableWrapper<FWRAPPED> wrapp(FWRAPPED wrapped){
            return new HashableWrapper<>(wrapped,hasher);
        }
    }
}
