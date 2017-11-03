package org.someth2say.taijitu.compare;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.someth2say.taijitu.config.interfaces.IComparisonCfg;
import org.someth2say.taijitu.config.interfaces.ISourceCfg;
import org.someth2say.taijitu.tuple.ComparableTuple;
import org.someth2say.taijitu.util.ImmutablePair;

public abstract class ComparisonResult {

	public static class SourceAndTuple extends ImmutablePair<ISourceCfg, ComparableTuple> {
		public SourceAndTuple(ISourceCfg iSource, ComparableTuple tuple) {
			super(iSource, tuple);
		}
	}

	public abstract class Mismatch {
		final Map<ISourceCfg, ComparableTuple> entries;

		public Mismatch(Collection<SourceAndTuple> existing) {
			entries = new HashMap<>(existing.size());
			existing.forEach(qat -> entries.put(qat.getKey(), qat.getValue()));
		}
	}

	public class Difference extends Mismatch {
		public Difference(Collection<SourceAndTuple> different) {
			super(different);
		}
	}

	public class Missing extends Mismatch {
		public Missing(Collection<SourceAndTuple> existing) {
			super(existing);
		}
	}

	protected final IComparisonCfg comparisonConfigIface;

	// final private Collection<Pair<QueryAndTuple, QueryAndTuple>> different;
	// final private Collection<QueryAndTuple> disjoint;

	private final Collection<Mismatch> mismatches;

	ComparisonResult(final IComparisonCfg comparisonConfigIface, final Collection<Mismatch> mismatches) {
		this.comparisonConfigIface = comparisonConfigIface;
		this.mismatches = mismatches;
		// this.different = different;
		// this.disjoint = disjoint;
	}

	public IComparisonCfg getComparisonConfig() {
		return comparisonConfigIface;
	}

	// public Collection<Pair<QueryAndTuple, QueryAndTuple>> getDifferent() {
	// return different;
	// }
	//
	// public Collection<QueryAndTuple> getDisjoint() {
	// return disjoint;
	// }

	public void addDifference(final SourceAndTuple first, final SourceAndTuple second) {
		// different.add(new ImmutablePair<>(first, second));

		getMismatches().add(new Difference(List.of(first, second)));
	}

	public void addDisjoint(final SourceAndTuple element) {
		// disjoint.add(element);

		getMismatches().add(new Missing(List.of(element)));
	}

	public void addAllDisjoint(Collection<SourceAndTuple> entries) {
		// disjoint.addAll(entries);

		entries.forEach(this::addDisjoint);
	}

	public Collection<Mismatch> getMismatches() {
		return mismatches;
	}
}
