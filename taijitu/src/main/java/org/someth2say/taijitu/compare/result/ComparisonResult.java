package org.someth2say.taijitu.compare.result;


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
    }

    abstract class Mismatch<MMT> {
        final Map<Object, MMT> entries;

        Mismatch(Collection<SourceIdAndComposite<MMT>> entries) {
            this.entries = new HashMap<>(entries.size());
            entries.forEach(srcAndComposite -> this.entries.put(srcAndComposite.sourceId, srcAndComposite.composite));
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName()+": "+entries.toString();
        }
    }

    class Difference<DT> extends Mismatch<DT> {
        Difference(Collection<SourceIdAndComposite<DT>> different) {
            super(different);
        }
    }

    class Missing<MT> extends Mismatch<MT> {
        Missing(Collection<SourceIdAndComposite<MT>> existing) {
            super(existing);
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
}
