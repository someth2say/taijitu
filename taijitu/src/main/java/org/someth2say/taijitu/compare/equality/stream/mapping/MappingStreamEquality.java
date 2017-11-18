package org.someth2say.taijitu.compare.equality.stream.mapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.compare.equality.CategorizerEquality;
import org.someth2say.taijitu.compare.equality.ComparableCategorizerEquality;
import org.someth2say.taijitu.compare.equality.Equality;
import org.someth2say.taijitu.compare.equality.stream.AbstractStreamEquality;
import org.someth2say.taijitu.compare.equality.stream.StreamEquality;
import org.someth2say.taijitu.compare.equality.wrapper.CategorizerEqualityWrapper;
import org.someth2say.taijitu.compare.result.ComparisonResult;
import org.someth2say.taijitu.compare.result.SourceIdAndComposite;
import org.someth2say.taijitu.compare.result.SynchronizedComparisonResult;
import org.someth2say.taijitu.discarter.TimeBiDiscarter;
import org.someth2say.taijitu.ui.config.interfaces.IStrategyCfg;
import org.someth2say.taijitu.util.StreamUtil;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.stream.Stream;

/**
 * Created by Jordi Sola on 02/03/2017.
 */
public class MappingStreamEquality<T> extends AbstractStreamEquality<T> implements StreamEquality<T> {
    public static final String NAME = "mapping";
    private static final Logger logger = LoggerFactory.getLogger(MappingStreamEquality.class);

    public MappingStreamEquality(Equality<T> equality, ComparableCategorizerEquality<T> categorizer) {
        super(equality, categorizer);
    }

    @Override
    public String getName() {
        return NAME;
    }

    public ComparisonResult<T> match(Stream<T> source, Object sourceId, Stream<T> target, Object targetId) {
        CategorizerEquality<T> categorizer = getCategorizer();
        Equality<T> equality = getEquality();
        return match(source, sourceId, target, targetId, categorizer, equality);
    }

    public static <T> ComparisonResult<T> match(Stream<T> source, Object sourceId, Stream<T> target, Object targetId,
                                                CategorizerEquality<T> categorizer, Equality<T> equality) {
        BiFunction<T, T, Boolean> equalityFunc = equality::equals;
        return matchParallel(source, sourceId, target, targetId, categorizer, equalityFunc);
    }

    public static <T> ComparisonResult<T> match(Map<Object, Stream<T>> streams, CategorizerEquality<T> categorizer,
                                                BiFunction<T, T, Boolean> equality) {
        if (streams.size() < 2)
            throw new RuntimeException("Need at least two streams to compare");

        if (streams.size() > 2)
            logger.info("Provided {} streams, but only 2 first will be compared.", streams.size());

        Iterator<Entry<Object, Stream<T>>> iterator = streams.entrySet().iterator();
        Entry<Object, Stream<T>> source = iterator.next();
        Entry<Object, Stream<T>> target = iterator.next();

        return matchParallel(source.getValue(), source.getKey(), target.getValue(), target.getKey(), categorizer, equality);
    }

    public static <T> ComparisonResult<T> match(Stream<T> source, Object sourceID, Stream<T> target, Object targetId,
                                                CategorizerEquality<T> categorizer, BiFunction<T, T, Boolean> equality) {

        SynchronizedComparisonResult<T> result = new SynchronizedComparisonResult<>();
        Iterator<T> sourceIt = source.iterator();
        Iterator<T> targetIt = target.iterator();

        Map<CategorizerEqualityWrapper<T>, SourceIdAndComposite<T>> sharedMap = new ConcurrentHashMap<>();

        final int recordCount = 0;
        final TimeBiDiscarter<String, Object[]> timedLogger = new TimeBiDiscarter<String, Object[]>(1000, logger::debug);
        StreamUtil.zip(source, target,
                (t1, t2) -> {
                    map(t1, timedLogger, recordCount);
                    map(t2, timedLogger, recordCount);
                    return null;
                },
                (t1) -> {
                    map(t1, timedLogger, recordCount);
                    return null;
                },
                (t2) -> {
                    map(t2, timedLogger, recordCount);
                    return null;
                });

        // 2.- When both mapping tasks are completed, remaining data are source/target
        // only
        final Collection<SourceIdAndComposite<T>> entries = sharedMap.values();
        entries.stream().forEach(sc -> result.addDisjoint(sc.getSourceId(), sc.getComposite()));


        return result;
    }

    public static <T> void map(T thisRecord, TimeBiDiscarter<String, Object[]> timedLogger, int recordCount) {
        SourceIdAndComposite<T> thisQueryAndTuple = new SourceIdAndComposite<>(sourceId, thisRecord);
        CategorizerEqualityWrapper<T> wrap = categorizer.wrap(thisRecord);
        SourceIdAndComposite<T> otherQueryAndTuple = sharedMap.putIfAbsent(wrap, thisQueryAndTuple);
        if (otherQueryAndTuple != null) {
            // we have a key matchParallel ...
            sharedMap.remove(wrap);
            final TMT otherRecord = otherQueryAndTuple.getComposite();
            if (!equalityFunc.apply(thisRecord, otherRecord)) {
                // ...and contents differ
                result.addDifference(sourceId, thisRecord, otherQueryAndTuple.getSourceId(), otherRecord);
            }
        }
        timedLogger.accept("Processed {} records from source {}", new Object[]{recordCount, sourceId});
    }

    public static <T> ComparisonResult<T> matchParallel(Stream<T> source, Object sourceID, Stream<T> target, Object targetId,
                                                        CategorizerEquality<T> categorizer, BiFunction<T, T, Boolean> equality) {
        // 1.- Build/run mapping tasks
        SynchronizedComparisonResult<T> result = new SynchronizedComparisonResult<>();
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
        entries.stream().forEach(sc -> result.addDisjoint(sc.getSourceId(), sc.getComposite()));

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

    public static IStrategyCfg defaultConfig() {
        return () -> MappingStreamEquality.NAME;
    }

    private static class TupleMapperExt<TMT> implements Runnable {
        private final Iterator<TMT> source;
        private final Map<CategorizerEqualityWrapper<TMT>, SourceIdAndComposite<TMT>> sharedMap;
        private final SynchronizedComparisonResult<TMT> result;
        private final Object sourceId;
        private final CategorizerEquality<TMT> categorizer;
        private final BiFunction<TMT, TMT, Boolean> equalityFunc;

        private TupleMapperExt(final Iterator<TMT> source,
                               final Map<CategorizerEqualityWrapper<TMT>, SourceIdAndComposite<TMT>> sharedMap,
                               final SynchronizedComparisonResult<TMT> result, Object sourceId, CategorizerEquality<TMT> categorizer,
                               BiFunction<TMT, TMT, Boolean> equalityFunc) {
            this.source = source;
            this.sharedMap = sharedMap;
            this.result = result;
            this.sourceId = sourceId;
            this.categorizer = categorizer;
            this.equalityFunc = equalityFunc;
        }

        @Override
        public void run() {
            TimeBiDiscarter<String, Object[]> timedLogger = new TimeBiDiscarter<>(1000, logger::debug);
            int recordCount = 0;
            TMT thisRecord = getNextRecordOrNull(source);
            while (thisRecord != null) {
                recordCount++;

                map(thisRecord, timedLogger, recordCount);

                thisRecord = getNextRecordOrNull(source);
            }
        }
    }
}
