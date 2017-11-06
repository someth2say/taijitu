package org.someth2say.taijitu.compare.equality.external;

public class EqualityWrapper<T> {

    private final T wrapped;
    private final ExternalEquality<T> externalEquality;

    public EqualityWrapper(T wrapped, ExternalEquality<T> externalEquality) {
        this.wrapped = wrapped;
        this.externalEquality = externalEquality;
    }

    public T unwrapp() {
        return wrapped;
    }

    @Override
    public boolean equals(Object obj) {
        if (wrapped==null){
            return obj==null;
        }

        if (wrapped.getClass().isAssignableFrom(obj.getClass())) {
            return externalEquality.equals(wrapped,(T)obj);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return externalEquality.hashCode(wrapped);
    }

    public T getWrapped() {
        return wrapped;
    }

    public ExternalEquality<T> getExternalEquality() {
        return externalEquality;
    }
}
