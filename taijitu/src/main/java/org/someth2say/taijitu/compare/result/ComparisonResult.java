package org.someth2say.taijitu.compare.result;

import org.someth2say.taijitu.util.ImmutablePair;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class ComparisonResult<T> {

    public static class SourceIdAndStructure<T> extends ImmutablePair<Object, T> {
        public SourceIdAndStructure(Object iSource, T tuple) {
            super(iSource, tuple);
        }

        @Override
        public String toString() {
            return "[SourceId: " + getKey() + "-> Composite: " + getValue() + "]";
        }
    }

    public abstract class Mismatch<T> {
        final Map<Object, T> entries;

        public Mismatch(Collection<SourceIdAndStructure<T>> existing) {
            entries = new HashMap<>(existing.size());
            existing.forEach(qat -> entries.put(qat.getKey(), qat.getValue()));
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName()+": "+entries.toString();
        }
    }

    public class Difference<T> extends Mismatch<T> {
        public Difference(Collection<SourceIdAndStructure<T>> different) {
            super(different);
        }
    }

    public class Missing<T> extends Mismatch<T> {
        public Missing(Collection<SourceIdAndStructure<T>> existing) {
            super(existing);
        }
    }

    private final Collection<Mismatch<T>> mismatches;

    ComparisonResult(final Collection<Mismatch<T>> mismatches) {
        this.mismatches = mismatches;
    }

    public void addDifference(final SourceIdAndStructure<T> first, final SourceIdAndStructure<T> second) {
        getMismatches().add(new Difference<>(Arrays.asList(first, second)));
    }

    public void addDisjoint(final SourceIdAndStructure<T> element) {
        getMismatches().add(new Missing<>(Arrays.asList(element)));
    }

    public void addAllDisjoint(Collection<SourceIdAndStructure<T>> entries) {
        entries.forEach(this::addDisjoint);
    }

    public Collection<Mismatch<T>> getMismatches() {
        return mismatches;
    }
}
