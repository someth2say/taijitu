package org.someth2say.taijitu.compare;

import org.someth2say.taijitu.config.ComparisonConfig;
import org.someth2say.taijitu.tuple.ComparableTuple;
import org.someth2say.taijitu.util.Pair;

import java.util.Collection;

public abstract class ComparisonResult {
    protected final ComparisonConfig comparisonConfig;

    final private Collection<Pair<ComparableTuple, ComparableTuple>> different;
    final private Collection<ComparableTuple> sourceOnly;
    final private Collection<ComparableTuple> targetOnly;

    ComparisonResult(final ComparisonConfig comparisonConfig, final Collection<Pair<ComparableTuple, ComparableTuple>> different, final Collection<ComparableTuple> sourceOnly, final Collection<ComparableTuple> targetOnly) {
        this.comparisonConfig = comparisonConfig;
        this.different = different;
        this.sourceOnly = sourceOnly;
        this.targetOnly = targetOnly;
    }

    public ComparisonConfig getComparisonConfig() {
        return comparisonConfig;
    }

    public Collection<ComparableTuple> getSourceOnly() {
        return sourceOnly;
    }

    public Collection<ComparableTuple> getTargetOnly() {
        return targetOnly;
    }

    public Collection<Pair<ComparableTuple, ComparableTuple>> getDifferent() {
        return different;
    }


}
