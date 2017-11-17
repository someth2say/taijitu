package org.someth2say.taijitu.compare.result;


import org.apache.commons.lang3.StringUtils;

import java.util.*;

public abstract class ComparisonResult<T> {

    public static class SourceIdAndComposite<T> {
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

    private final Collection<Mismatch<T>> mismatches;

    ComparisonResult(final Collection<Mismatch<T>> mismatches) {
        this.mismatches = mismatches;
    }

    public void addDifference(final SourceIdAndComposite<T> first, final SourceIdAndComposite<T> second) {
        getMismatches().add(new Difference<>(Arrays.asList(first, second)));
    }

    public void addDisjoint(final SourceIdAndComposite<T> element) {
        getMismatches().add(new Missing<>(Collections.singletonList(element)));
    }

    public void addAllDisjoint(Collection<SourceIdAndComposite<T>> entries) {
        entries.forEach(this::addDisjoint);
    }

    public Collection<Mismatch<T>> getMismatches() {
        return mismatches;
    }

    @Override
    public String toString() {
        return StringUtils.join(mismatches.toString(),",");
    }


}
