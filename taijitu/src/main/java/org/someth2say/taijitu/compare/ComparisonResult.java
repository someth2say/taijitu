package org.someth2say.taijitu.compare;

import java.util.*;

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

	private final Collection<Mismatch> mismatches;

	ComparisonResult(final Collection<Mismatch> mismatches) {
		this.mismatches = mismatches;
	}

	public void addDifference(final SourceAndTuple first, final SourceAndTuple second) {
		getMismatches().add(new Difference(Arrays.asList(first, second)));
	}

	public void addDisjoint(final SourceAndTuple element) {
		getMismatches().add(new Missing(Arrays.asList(element)));
	}

	public void addAllDisjoint(Collection<SourceAndTuple> entries) {
		entries.forEach(this::addDisjoint);
	}

	public Collection<Mismatch> getMismatches() {
		return mismatches;
	}
}
