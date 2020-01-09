package org.someth2say.taijitu.equality.wrapper;

public abstract class Wrapper<WRAPPED> {
    private final WRAPPED wrapped;

    Wrapper(WRAPPED wrapped) {
        this.wrapped = wrapped;
    }

    public WRAPPED getWraped() {
        return wrapped;
    }

    public interface Factory<WRAPPED> {
        Wrapper<WRAPPED> wrap(WRAPPED wrapped);
    }

}
