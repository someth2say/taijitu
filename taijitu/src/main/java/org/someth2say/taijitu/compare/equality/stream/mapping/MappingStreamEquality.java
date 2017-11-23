package org.someth2say.taijitu.compare.equality.stream.mapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.compare.equality.CategorizerEquality;
import org.someth2say.taijitu.compare.equality.ComparableCategorizerEquality;
import org.someth2say.taijitu.compare.equality.Equality;
import org.someth2say.taijitu.compare.equality.stream.AbstractStreamEquality;
import org.someth2say.taijitu.compare.equality.stream.StreamEquality;
import org.someth2say.taijitu.compare.equality.wrapper.CategorizerEqualityWrapper;
import org.someth2say.taijitu.compare.result.*;
import org.someth2say.taijitu.discarter.TimeBiDiscarter;
import org.someth2say.taijitu.ui.config.interfaces.IStrategyCfg;
import org.someth2say.taijitu.util.StreamUtil;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
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
        return matchSequential(source, sourceId, target, targetId, categorizer, equality);
    }

    public static <T> ComparisonResult<T> match(Map<Object, Stream<T>> streams, CategorizerEquality<T> categorizer, Equality<T> equality) {
        if (streams.size() < 2)
            throw new RuntimeException("Need at least two streams to compare");

        if (streams.size() > 2)
            logger.info("Provided {} streams, but only 2 first will be compared.", streams.size());

        Iterator<Entry<Object, Stream<T>>> iterator = streams.entrySet().iterator();
        Entry<Object, Stream<T>> source = iterator.next();
        Entry<Object, Stream<T>> target = iterator.next();

        //TODO: Find a way to discriminate (config)?
        return matchSequential(source.getValue(), source.getKey(), target.getValue(), target.getKey(), categorizer, equality);
//        return matchParallel(source.getValue(), source.getKey(), target.getValue(), target.getKey(), categorizer, equality);
    }

    public static <T> ComparisonResult<T> matchSequential(Stream<T> source, Object sourceId, Stream<T> target, Object targetId,
                                                          CategorizerEquality<T> categorizer, Equality<T> equality) {

        SynchronizedComparisonResult<T> result = new SynchronizedComparisonResult<>();

        Map<CategorizerEqualityWrapper<T>, SourceIdAndComposite<T>> sharedMap = new ConcurrentHashMap<>();
        final int recordCount = 0;
        final TimeBiDiscarter<String, Object[]> timedLogger = new TimeBiDiscarter<>(1000, logger::debug);

        //TODO: This is shitty, should create an alternatting stream
        StreamUtil.zip(source, target,
                (t1, t2) -> {
                    Difference<T> difference1 = map(t1, timedLogger, recordCount, sourceId, categorizer, sharedMap, equality);
                    Difference<T> difference2 = map(t2, timedLogger, recordCount, targetId, categorizer, sharedMap, equality);
                    return Stream.of(difference1, difference2);
                },
                (t1) -> Stream.of(map(t1, timedLogger, recordCount, sourceId, categorizer, sharedMap, equality)),
                (t2) -> Stream.of(map(t2, timedLogger, recordCount, targetId, categorizer, sharedMap, equality))
        ).flatMap(Function.identity()).filter(Objects::nonNull).forEach(result::addDifference);

        // 2.- When both mapping tasks are completed, remaining data are source/target
        // only
        final Collection<SourceIdAndComposite<T>> entries = sharedMap.values();
        entries.stream().forEach(sc -> result.addDisjoint(categorizer, sc.getComposite()));

        return result;
    }

    private static <T> Difference<T> map(T thisRecord, TimeBiDiscarter<String, Object[]> timedLogger, int recordCount, Object sourceId,
                                         CategorizerEquality<T> categorizer, Map<CategorizerEqualityWrapper<T>, SourceIdAndComposite<T>> sharedMap,
                                         Equality<T> equality) {
        SourceIdAndComposite<T> thisQueryAndTuple = new SourceIdAndComposite<>(sourceId, thisRecord);
        CategorizerEqualityWrapper<T> wrap = categorizer.wrap(thisRecord);
        SourceIdAndComposite<T> otherQueryAndTuple = sharedMap.putIfAbsent(wrap, thisQueryAndTuple);
        if (otherQueryAndTuple != null) {
            // we have a key matchSequential ...
            sharedMap.remove(wrap);
            final T otherRecord = otherQueryAndTuple.getComposite();
            List<Mismatch> differences = equality.differences(thisRecord, otherRecord);
            if (differences != null && !differences.isEmpty()) {
                // ...and contents differ
                return new Difference<>(equality, thisRecord, otherRecord, differences);
            }
        }
        timedLogger.accept("Processed {} records from source {}", new Object[]{recordCount, sourceId});
        return null;
    }

    public static <T> ComparisonResult<T> matchParallel(Stream<T> source, Object sourceID, Stream<T> target, Object targetId,
                                                        CategorizerEquality<T> categorizer, Equality<T> equality) {
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
        entries.stream().forEach(sc -> result.addDisjoint(categorizer, sc.getComposite()));

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
        private final Equality<TMT> equality;

        private TupleMapperExt(final Iterator<TMT> source,
                               final Map<CategorizerEqualityWrapper<TMT>, SourceIdAndComposite<TMT>> sharedMap,
                               final SynchronizedComparisonResult<TMT> result, Object sourceId, CategorizerEquality<TMT> categorizer,
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

                Difference<TMT> difference = map(thisRecord, timedLogger, recordCount, sourceId, categorizer, sharedMap, equality);
                if (difference != null) {
                    result.addDifference(difference);
                }

                thisRecord = getNextRecordOrNull(source);
            }
        }
    }
}
