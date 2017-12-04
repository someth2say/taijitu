package org.someth2say.taijitu.compare.equality.impl.stream.mapping;

import java.util.Objects;

public class OrdinalAndComposite<T> {
    public int getOrdinal() {
        return ordinal;
    }

    public T getComposite() {
        return composite;
    }

    private final int ordinal;
    private final T composite;

    public OrdinalAndComposite(int sourceId, T composite) {
        this.ordinal = sourceId;
        this.composite = composite;
    }

    @Override
    public String toString() {
        return "["+ ordinal + "-> " + composite + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrdinalAndComposite)) return false;
        OrdinalAndComposite<?> that = (OrdinalAndComposite<?>) o;
        return Objects.equals(getOrdinal(), that.getOrdinal()) &&
                Objects.equals(getComposite(), that.getComposite());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOrdinal(), getComposite());
    }
}
