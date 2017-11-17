package org.someth2say.taijitu.compare.result;

import org.someth2say.taijitu.compare.result.ComparisonResult.SourceIdAndComposite;

import java.util.*;

public abstract class Mismatch<MMT> {
    final Map<Object, MMT> entries;

    Mismatch(Collection<SourceIdAndComposite<MMT>> entries) {
        this.entries = new HashMap<>(entries.size());
        entries.forEach(srcAndComposite -> this.entries.put(srcAndComposite.getSourceId(), srcAndComposite.getComposite()));
    }

    public Mismatch(SourceIdAndComposite<MMT>... entries) {
        this(Arrays.asList(entries));
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + entries.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mismatch)) return false;
        Mismatch<?> mismatch = (Mismatch<?>) o;
        //TODO: Warning, the order of entries is not relevant! Maybe shoudl use a Set...
        return Objects.equals(entries, mismatch.entries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entries);
    }
}
