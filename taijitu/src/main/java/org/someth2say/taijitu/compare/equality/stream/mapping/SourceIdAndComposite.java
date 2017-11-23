package org.someth2say.taijitu.compare.equality.stream.mapping;

import java.util.Objects;

public class SourceIdAndComposite<T> {
    public Object getSourceId() {
        return sourceId;
    }

    public T getComposite() {
        return composite;
    }

    private final Object sourceId;
    private final T composite;

    public SourceIdAndComposite(Object sourceId, T composite) {
        this.sourceId = sourceId;
        this.composite = composite;
    }

    @Override
    public String toString() {
        return "[SourceId: " + sourceId + "-> Composite: " + composite + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SourceIdAndComposite)) return false;
        SourceIdAndComposite<?> that = (SourceIdAndComposite<?>) o;
        return Objects.equals(getSourceId(), that.getSourceId()) &&
                Objects.equals(getComposite(), that.getComposite());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSourceId(), getComposite());
    }
}
