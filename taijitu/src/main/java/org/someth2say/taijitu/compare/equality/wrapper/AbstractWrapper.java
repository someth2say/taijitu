package org.someth2say.taijitu.compare.equality.wrapper;

public abstract class AbstractWrapper<T, EQ> {
    private final T wrapped;
    private final EQ equality;

    AbstractWrapper(T wrapped, EQ equality) {
        this.equality = equality;
        this.wrapped = wrapped;
    }

	public T unwrap() {
        return wrapped;
    }

	public EQ getEquality() {
        return equality;
    }
}
