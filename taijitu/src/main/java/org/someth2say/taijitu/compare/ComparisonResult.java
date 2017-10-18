package org.someth2say.taijitu.compare;

import org.someth2say.taijitu.config.ComparisonConfig;
import org.someth2say.taijitu.config.QueryConfig;
import org.someth2say.taijitu.tuple.ComparableTuple;
import org.someth2say.taijitu.util.ImmutablePair;
import org.someth2say.taijitu.util.Pair;

import java.util.Collection;

public abstract class ComparisonResult {
    protected final ComparisonConfig comparisonConfig;
    //TODO: Consider using maps indexed by query, instead of a Collection.
    final private Collection<Pair<QueryAndTuple, QueryAndTuple>> different;
    final private Collection<QueryAndTuple> disjoint;

    ComparisonResult(final ComparisonConfig comparisonConfig, final Collection<Pair<QueryAndTuple, QueryAndTuple>> different, Collection<QueryAndTuple> disjoint) {
        this.comparisonConfig = comparisonConfig;
        this.different = different;
        this.disjoint = disjoint;
    }

    public ComparisonConfig getComparisonConfig() {
        return comparisonConfig;
    }

    public Collection<Pair<QueryAndTuple, QueryAndTuple>> getDifferent() {
        return different;
    }

    public Collection<QueryAndTuple> getDisjoint() {
        return disjoint;
    }

    public static class QueryAndTuple extends ImmutablePair<QueryConfig, ComparableTuple> {
        public QueryAndTuple(QueryConfig queryConfig, ComparableTuple tuple) {
            super(queryConfig, tuple);
        }
    }

}
