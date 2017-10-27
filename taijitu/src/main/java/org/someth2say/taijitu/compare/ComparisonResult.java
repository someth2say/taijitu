package org.someth2say.taijitu.compare;

import org.someth2say.taijitu.config.ComparisonConfig;
import org.someth2say.taijitu.config.QuerySourceConfig;
import org.someth2say.taijitu.config.SourceConfig;
import org.someth2say.taijitu.tuple.ComparableTuple;
import org.someth2say.taijitu.util.ImmutablePair;
import org.someth2say.taijitu.util.Pair;

import java.util.Collection;

public abstract class ComparisonResult {
    protected final ComparisonConfig comparisonConfig;
    // TODO: Moving forward to stream API will require a single difference class, covering both different and disjoint.
    // TODO: Work out the structure for this kind of items. I.E. Disjoint, may be a Map<Query->Collection<Tuple>>, and Different may be a Collection<Map<Query->Tuple>>
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


    public static class QueryAndTuple extends ImmutablePair<SourceConfig, ComparableTuple> {
        public QueryAndTuple(SourceConfig querySourceConfig, ComparableTuple tuple) {
            super(querySourceConfig, tuple);
        }
    }

    public void addDifference(final QueryAndTuple first, final QueryAndTuple second) {
        different.add(new ImmutablePair<>(first, second));
    }

    public void addDisjoint(final QueryAndTuple element) {
        disjoint.add(element);
    }

    public void addAllDisjoint(Collection<QueryAndTuple> entries) {
        disjoint.addAll(entries);
    }
}
