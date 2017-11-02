package org.someth2say.taijitu.compare;

import org.someth2say.taijitu.config.interfaces.IComparisonCfg;
import org.someth2say.taijitu.config.interfaces.ISourceCfg;
import org.someth2say.taijitu.tuple.ComparableTuple;
import org.someth2say.taijitu.util.ImmutablePair;
import org.someth2say.taijitu.util.Pair;

import java.util.Collection;

public abstract class ComparisonResult {
    protected final IComparisonCfg comparisonConfigIface;
    // TODO: Moving forward to stream API will require a single difference class, covering both different and disjoint.
    // TODO: Work out the structure for this kind of items. I.E. Disjoint, may be a Map<Query->Collection<Tuple>>, and Different may be a Collection<Map<Query->Tuple>>
    final private Collection<Pair<QueryAndTuple, QueryAndTuple>> different;
    final private Collection<QueryAndTuple> disjoint;

    ComparisonResult(final IComparisonCfg comparisonConfigIface, final Collection<Pair<QueryAndTuple, QueryAndTuple>> different, Collection<QueryAndTuple> disjoint) {
        this.comparisonConfigIface = comparisonConfigIface;
        this.different = different;
        this.disjoint = disjoint;
    }

    public IComparisonCfg getComparisonConfig() {
        return comparisonConfigIface;
    }

    public Collection<Pair<QueryAndTuple, QueryAndTuple>> getDifferent() {
        return different;
    }

    public Collection<QueryAndTuple> getDisjoint() {
        return disjoint;
    }


    public static class QueryAndTuple extends ImmutablePair<ISourceCfg, ComparableTuple> {
        public QueryAndTuple(ISourceCfg iSource, ComparableTuple tuple) {
            super(iSource, tuple);
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
