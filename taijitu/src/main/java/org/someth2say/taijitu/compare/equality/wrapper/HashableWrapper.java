package org.someth2say.taijitu.compare.equality.wrapper;

import org.someth2say.taijitu.compare.equality.aspects.external.Hasher;
import org.someth2say.taijitu.compare.equality.aspects.internal.Hashable;

public class HashableWrapper<WRAPPED>
        extends EqualizableWrapper<WRAPPED>
        implements Hashable<Wrapper<WRAPPED>> {

    @Override
    public Hasher<WRAPPED> getEqualizer() {
        return hasher;
    }

    private final Hasher<WRAPPED> hasher;


    public HashableWrapper(WRAPPED wrapped, Hasher<WRAPPED> hasher) {
        super(wrapped, hasher);
        this.hasher =hasher;
    }

    @Override
    public int hashCode() {
        return getEqualizer().hash(getWraped());
    }

    public static class Factory<FWRAPPED> implements Wrapper.Factory<HashableWrapper<FWRAPPED>, FWRAPPED> {
        private final Hasher<FWRAPPED> hasher;

        public Factory(Hasher<FWRAPPED> hasher) {
            this.hasher = hasher;
        }

        @Override
        public HashableWrapper<FWRAPPED> wrap(FWRAPPED wrapped) {
            return new HashableWrapper<>(wrapped, hasher);
        }
    }
}
