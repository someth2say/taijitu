package org.someth2say.taijitu.compare.equality.stream.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.compare.equality.CategorizerEquality;
import org.someth2say.taijitu.compare.equality.ComparableCategorizerEquality;
import org.someth2say.taijitu.compare.equality.Equality;
import org.someth2say.taijitu.compare.equality.stream.AbstractStreamEquality;
import org.someth2say.taijitu.compare.equality.stream.ComparisonResult;
import org.someth2say.taijitu.compare.equality.stream.StreamEquality;
import org.someth2say.taijitu.compare.equality.wrapper.CategorizerEqualityWrapper;
import org.someth2say.taijitu.compare.result.Difference;
import org.someth2say.taijitu.compare.result.Mismatch;
import org.someth2say.taijitu.compare.result.Missing;
import org.someth2say.taijitu.discarter.TimeBiDiscarter;
import org.someth2say.taijitu.util.StreamUtil;

/**
 * Created by Jordi Sola on 02/03/2017.
 */
public class MappingStreamEquality<T> extends AbstractStreamEquality<T> implements StreamEquality<T> {
	private static final Logger logger = LoggerFactory.getLogger(MappingStreamEquality.class);

	public MappingStreamEquality(Equality<T> equality, ComparableCategorizerEquality<T> categorizer) {
		super(equality, categorizer);
	}

	@Override
	public List<Mismatch> match(Stream<T> source, Object sourceId, Stream<T> target, Object targetId) {
		// TODO: Find a way to discriminate (config)?
		// return matchParallel(source, sourceID, target, targetId, getCategorizer(),
		// getEquality());
		return matchSequential(source, sourceId, target, targetId, getCategorizer(), getEquality());
	}

	public static <T> List<Mismatch> matchSequential(Stream<T> source, Object sourceId, Stream<T> target,
			Object targetId, CategorizerEquality<T> categorizer, Equality<T> equality) {

		final List<Mismatch> result = Collections.synchronizedList(new ArrayList<>());

		Map<CategorizerEqualityWrapper<T>, SourceIdAndComposite<T>> sharedMap = new ConcurrentHashMap<>();
		final int recordCount = 0;
		final TimeBiDiscarter<String, Object[]> timedLogger = new TimeBiDiscarter<>(1000, logger::debug);

		Stream<Difference<T>> differences = StreamUtil
				.zip(source.map(c -> new SourceIdAndComposite<>(sourceId, c)),
						target.map(c -> new SourceIdAndComposite<>(targetId, c)))
				.map(sac -> map(sac.getComposite(), sac.getSourceId(), timedLogger, recordCount, categorizer, sharedMap,
						equality))
				.filter(Objects::nonNull);

		// 2.- When both mapping tasks are completed, remaining data are source/target
		// only
		Stream<Missing<T>> missings = sharedMap.values().stream()
				.map((SourceIdAndComposite<T> sac) -> new Missing<T>(categorizer, sac.getComposite()));
		// sharedMap.values().forEach(sc -> ComparisonResult.addMissing(result,
		// categorizer, sc.getComposite()));

		return Stream.concat(differences, missings).collect(Collectors.toList());
	}

	private static <T> Difference<T> map(T thisComposite, Object sourceId,
			TimeBiDiscarter<String, Object[]> timedLogger, int recordCount, CategorizerEquality<T> categorizer,
			Map<CategorizerEqualityWrapper<T>, SourceIdAndComposite<T>> sharedMap, Equality<T> equality) {
		timedLogger.accept("Processed {} records so far.", new Object[] { recordCount });
		CategorizerEqualityWrapper<T> wrap = categorizer.wrap(thisComposite);
		SourceIdAndComposite<T> otherSaC = sharedMap.putIfAbsent(wrap,
				new SourceIdAndComposite<>(sourceId, thisComposite));
		if (otherSaC != null) {
			// we have a key matchSequential ...
			sharedMap.remove(wrap);
			final T otherComposite = otherSaC.getComposite();
			List<Mismatch> differences = equality.differences(thisComposite, otherComposite);
			if (differences != null && !differences.isEmpty()) {
				// ...and contents differ
				// TODO: the order of the composites is not defined here! Need to use ids to
				// sort correctly.
				return new Difference<>(equality, thisComposite, otherComposite, differences);
				// return ComparisonResult.addDifference(result, equality, thisComposite,
				// otherComposite);
			}
		}
		return null;
	}

	public static <T> List<Mismatch> matchParallel(Stream<T> source, Object sourceID, Stream<T> target, Object targetId,
			CategorizerEquality<T> categorizer, Equality<T> equality) {
		// 1.- Build/run mapping tasks
		List<Mismatch> result = Collections.synchronizedList(new ArrayList<>());
		Iterator<T> sourceIt = source.iterator();
		Iterator<T> targetIt = target.iterator();

		// TODO: Another option is running queries/pages alternating, so we can
		// "restrict" memory usage, but only using a single thread
		final ExecutorService executorService = Executors.newFixedThreadPool(2);
		Map<CategorizerEqualityWrapper<T>, SourceIdAndComposite<T>> sharedMap = new ConcurrentHashMap<>();
		Runnable sourceMapper = new TupleMapperExt<>(sourceIt, sharedMap, result, sourceID, categorizer, equality);
		Runnable targetMapper = new TupleMapperExt<>(targetIt, sharedMap, result, targetId, categorizer, equality);

		executorService.submit(sourceMapper);// Map source
		executorService.submit(targetMapper);// Map target

		shutdownAndAwaitTermination(executorService);

		// 2.- When both mapping tasks are completed, remaining data are source/target
		// only
		final Collection<SourceIdAndComposite<T>> entries = sharedMap.values();
		entries.stream().forEach(sc -> ComparisonResult.addMissing(result, categorizer, sc.getComposite()));

		return result;
	}

	private static void shutdownAndAwaitTermination(ExecutorService pool) {
		pool.shutdown(); // Disable new tasks from being submitted
		try {
			// Wait a while for existing tasks to terminate
			while (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
				logger.info("Waiting for mapping stream to complete.");
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			pool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}

	private static class TupleMapperExt<TMT> implements Runnable {
		private final Iterator<TMT> source;
		private final Map<CategorizerEqualityWrapper<TMT>, SourceIdAndComposite<TMT>> sharedMap;
		private final List<Mismatch> result;
		private final Object sourceId;
		private final CategorizerEquality<TMT> categorizer;
		private final Equality<TMT> equality;

		private TupleMapperExt(final Iterator<TMT> source,
				final Map<CategorizerEqualityWrapper<TMT>, SourceIdAndComposite<TMT>> sharedMap,
				final List<Mismatch> result, Object sourceId, CategorizerEquality<TMT> categorizer,
				Equality<TMT> equality) {
			this.source = source;
			this.sharedMap = sharedMap;
			this.result = result;
			this.sourceId = sourceId;
			this.categorizer = categorizer;
			this.equality = equality;
		}

		@Override
		public void run() {
			TimeBiDiscarter<String, Object[]> timedLogger = new TimeBiDiscarter<>(1000, logger::debug);
			int recordCount = 0;
			TMT thisRecord = getNextRecordOrNull(source);
			while (thisRecord != null) {
				recordCount++;
				Difference<TMT> difference = map(thisRecord, sourceId, timedLogger, recordCount, categorizer, sharedMap,
						equality);
				if (difference != null) {
					result.add(difference);
				}
				thisRecord = getNextRecordOrNull(source);
			}
		}
	}
}
