package org.someth2say.taijitu.compare.result;

import org.apache.commons.lang3.StringUtils;
import org.someth2say.taijitu.compare.equality.external.Equality;

import java.util.*;

public abstract class Mismatch<MMT> {
    private final Equality<MMT> cause;
    private final List<MMT> entries;
    private final List<Mismatch<?>> underlyingMismatches;


    public Mismatch(Equality<MMT> cause, List<MMT> entries, List<Mismatch<?>> underlyingMismatches) {
        this.cause = cause;
        this.entries = entries;
        this.underlyingMismatches = underlyingMismatches;
    }

    public Mismatch(Equality<MMT> cause, List<MMT> entries) {
        this(cause, entries, null);
    }

    public Mismatch(Equality<MMT> cause, MMT composite, List<Mismatch<?>> underlyingMismatches) {
        this(cause, Collections.singletonList(composite), underlyingMismatches);
    }

    public Mismatch(Equality<MMT> cause, MMT composite) {
        this(cause, composite, (List<Mismatch<?>>) null);
    }

    public Mismatch(Equality<MMT> cause, MMT composite, MMT composite2) {
        this(cause, composite, composite2, null);
    }

    public Mismatch(Equality<MMT> cause, MMT composite, MMT composite2, List<Mismatch<?>> underlyingMismatches) {
        this(cause, Arrays.asList(composite, composite2), underlyingMismatches);
    }


    @Override
    public String toString() {
        if (underlyingMismatches != null) {
            return this.getClass().getSimpleName() + ": " + cause.toString() + "(" + StringUtils.join(underlyingMismatches, ",") + ")";
        } else {
            return this.getClass().getSimpleName() + ": " + cause.toString() + "(" + StringUtils.join(entries, "<>") + ")";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mismatch)) return false;
        Mismatch<?> mismatch = (Mismatch<?>) o;
        return Objects.equals(cause, mismatch.cause) &&
                Objects.equals(entries, mismatch.entries)
                && Objects.equals(underlyingMismatches, mismatch.underlyingMismatches);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entries, cause, underlyingMismatches);
    }
}
