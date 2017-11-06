package org.someth2say.taijitu.compare;

import java.util.*;

import org.someth2say.taijitu.config.interfaces.IComparisonCfg;
import org.someth2say.taijitu.config.interfaces.ISourceCfg;
import org.someth2say.taijitu.tuple.ComparableTuple;
import org.someth2say.taijitu.util.ImmutablePair;

public abstract class ComparisonResult<T> {

	public static class SourceAndTuple<T> extends ImmutablePair<ISourceCfg, T> {
		public SourceAndTuple(ISourceCfg iSource, T tuple) {
			super(iSource, tuple);
		}
	}

	public abstract class Mismatch<T> {
		final Map<ISourceCfg, T> entries;

		public Mismatch(Collection<SourceAndTuple<T>> existing) {
			entries = new HashMap<>(existing.size());
			existing.forEach(qat -> entries.put(qat.getKey(), qat.getValue()));
		}
	}

	public class Difference<T> extends Mismatch<T> {
		public Difference(Collection<SourceAndTuple<T>> different) {
			super(different);
		}
	}

	public class Missing<T> extends Mismatch<T> {
		public Missing(Collection<SourceAndTuple<T>> existing) {
			super(existing);
		}
	}

	private final Collection<Mismatch<T>> mismatches;

	ComparisonResult(final Collection<Mismatch<T>> mismatches) {
		this.mismatches = mismatches;
	}

	public void addDifference(final SourceAndTuple<T> first, final SourceAndTuple<T> second) {
		getMismatches().add(new Difference<>(Arrays.asList(first, second)));
	}

	public void addDisjoint(final SourceAndTuple<T> element) {
		getMismatches().add(new Missing<>(Arrays.asList(element)));
	}

	public void addAllDisjoint(Collection<SourceAndTuple<T>> entries) {
		entries.forEach(this::addDisjoint);
	}

	public Collection<Mismatch<T>> getMismatches() {
		return mismatches;
	}
}
