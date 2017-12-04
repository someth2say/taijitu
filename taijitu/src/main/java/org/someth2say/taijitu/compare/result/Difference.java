package org.someth2say.taijitu.compare.result;

import org.apache.commons.lang.StringUtils;
import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;

import java.util.*;

public abstract class Difference<MMT> {
    private final Equalizer<MMT> cause;
    private final List<MMT> entries;
    private final List<Difference<?>> underlyingDifferences;


    public Difference(Equalizer<MMT> cause, List<MMT> entries, List<Difference<?>> underlyingDifferences) {
        this.cause = cause;
        this.entries = entries;
        this.underlyingDifferences = underlyingDifferences;
    }

  /*  public Difference(Equalizer<MMT> cause, List<MMT> entries) {
        this(cause, entries, Collections.emptyList());
    }
*/
    public Difference(Equalizer<MMT> cause, MMT composite, List<Difference<?>> underlyingDifferences) {
        this(cause, Collections.singletonList(composite), underlyingDifferences);
    }

    public Difference(Equalizer<MMT> cause, MMT composite) {
        this(cause, composite, Collections.emptyList());
    }

    public Difference(Equalizer<MMT> cause, MMT composite, MMT composite2) {
        this(cause, composite, composite2, Collections.emptyList());
    }

    public Difference(Equalizer<MMT> cause, MMT composite, MMT composite2, List<Difference<?>> underlyingDifferences) {
        this(cause, Arrays.asList(composite, composite2), underlyingDifferences);
    }


    @Override
    public String toString() {
        if (underlyingDifferences != null) {
            return this.getClass().getSimpleName() + ": " + cause.toString() + "(" + StringUtils.join(underlyingDifferences, ",") + ")";
        } else {
            return this.getClass().getSimpleName() + ": " + cause.toString() + "(" + StringUtils.join(entries, "<>") + ")";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Difference)) return false;
        Difference<?> difference = (Difference<?>) o;
        return Objects.equals(cause, difference.cause) &&
                Objects.equals(entries, difference.entries)
                && Objects.equals(underlyingDifferences, difference.underlyingDifferences);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entries, cause, underlyingDifferences);
    }
}
