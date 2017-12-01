package org.someth2say.taijitu.compare.equality.wrapper;

public abstract class AbstractEqualityWrapper<T, EQ> implements Wrapper<T,EQ> {
    protected final T wrapped;
    protected final EQ equality;

    public AbstractEqualityWrapper(T wrapped, EQ equality) {
        this.equality = equality;
        this.wrapped = wrapped;
    }

    public T getWrapped() {
        return wrapped;
    }

    public EQ getEquality() {
        return equality;
    }
}
