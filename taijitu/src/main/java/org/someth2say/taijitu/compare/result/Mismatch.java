package org.someth2say.taijitu.compare.result;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class Mismatch<MMT> {
    final Map<Object, MMT> entries;

    public Mismatch(Map<Object, MMT> entries) {
        this.entries = entries;
    }

    public Mismatch(Object id, MMT composite) {
        this.entries = Collections.singletonMap(id, composite);
    }

    public Mismatch(Object id, MMT composite, Object id2, MMT composite2) {
        this.entries = new HashMap<>(2);
        this.entries.put(id,composite);
        this.entries.put(id2,composite2);
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
