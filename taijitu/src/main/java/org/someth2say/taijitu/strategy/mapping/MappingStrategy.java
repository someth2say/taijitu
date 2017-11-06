package org.someth2say.taijitu.strategy.mapping;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.ComparisonContext;
import org.someth2say.taijitu.compare.ComparisonResult;
import org.someth2say.taijitu.compare.ComparisonResult.SourceAndTuple;
import org.someth2say.taijitu.compare.SynchronizedComparisonResult;
import org.someth2say.taijitu.compare.equality.external.EqualityWrapper;
import org.someth2say.taijitu.compare.equality.external.ExternalEquality;
import org.someth2say.taijitu.config.interfaces.ISourceCfg;
import org.someth2say.taijitu.config.interfaces.IStrategyCfg;
import org.someth2say.taijitu.source.Source;
import org.someth2say.taijitu.strategy.AbstractComparisonStrategy;
import org.someth2say.taijitu.strategy.ComparisonStrategy;
import org.someth2say.taijitu.tuple.ComparableTuple;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jordi Sola on 02/03/2017.
 */
public class MappingStrategy extends AbstractComparisonStrategy implements ComparisonStrategy {
    public static final String NAME = "mapping";
    private static final Logger logger = Logger.getLogger(MappingStrategy.class);

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public <T extends ComparableTuple> ComparisonResult runComparison(Source<T> source, Source<T> target, ComparisonContext comparisonContext){
        SynchronizedComparisonResult<T> result = new SynchronizedComparisonResult<>();

        //1.- Build/run mapping tasks
        //TODO: Another option is running queries/pages alternating, so we can "restrict" memory usage, but only using a single thread
        final ExecutorService executorService = Executors.newFixedThreadPool(2);
        Map<T, SourceAndTuple<T>> sharedMap = new ConcurrentHashMap<>();
        Runnable sourceMapper = new TupleMapper<>(source.iterator(), sharedMap, result, source.getConfig());
        Runnable targetMapper = new TupleMapper<>(target.iterator(), sharedMap, result, target.getConfig());

        executorService.submit(sourceMapper);// Map source
        executorService.submit(targetMapper);// Map target

        shutdownAndAwaitTermination(executorService);

        //2.- When both mapping tasks are completed, remaining data are source/target only
        final Collection<SourceAndTuple<T>> entries = sharedMap.values();
        result.addAllDisjoint(entries);

        return result;
    }

    @Override
    public <T> ComparisonResult runExternalComparison(Source<T> source, Source<T> target, ExternalEquality<T> externalCategorizer, ExternalEquality<T> externalEquality) {
        SynchronizedComparisonResult<T> result = new SynchronizedComparisonResult<>();

        //1.- Build/run mapping tasks
        //TODO: Another option is running queries/pages alternating, so we can "restrict" memory usage, but only using a single thread
        final ExecutorService executorService = Executors.newFixedThreadPool(2);
        Map<EqualityWrapper<T>, SourceAndTuple<T>> sharedMap = new ConcurrentHashMap<>();
        Runnable sourceMapper = new TupleMapperExt<>(source.iterator(), sharedMap, result, source.getConfig(), externalCategorizer, externalEquality);
        Runnable targetMapper = new TupleMapperExt<>(target.iterator(), sharedMap, result, target.getConfig(), externalCategorizer, externalEquality);
        executorService.submit(sourceMapper);// Map source
        executorService.submit(targetMapper);// Map target

        shutdownAndAwaitTermination(executorService);

        //2.- When both mapping tasks are completed, remaining data are source/target only
        final Collection<SourceAndTuple<T>> entries = sharedMap.values();
        result.addAllDisjoint(entries);

        return result;
    }

    private void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            while (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                logger.info("Waiting for mapping strategy to complete.");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    public static IStrategyCfg defaultConfig() {
        return () -> MappingStrategy.NAME;
    }

    private class TupleMapper<T extends ComparableTuple>  implements Runnable {
        private final Iterator<T> tupleIterator;
        private final Map<T, SourceAndTuple<T>> sharedSet;
        private final SynchronizedComparisonResult<T> result;
        private final ISourceCfg iSource;

        private TupleMapper(final Iterator<T> tupleIterator, final Map<T, SourceAndTuple<T>> sharedMap, final SynchronizedComparisonResult<T> result, ISourceCfg iSource) {
            this.tupleIterator = tupleIterator;
            this.sharedSet = sharedMap;
            this.result = result;
            this.iSource = iSource;
        }

        @Override
        public void run() {
            for (T thisRecord = getNextRecord(tupleIterator); thisRecord != null; thisRecord = getNextRecord(tupleIterator)) {

                SourceAndTuple<T> thisQueryAndTuple = new SourceAndTuple<>(iSource, thisRecord);
                final SourceAndTuple<T> otherQueryAndTuple = sharedSet.putIfAbsent(thisRecord, thisQueryAndTuple);
                if (otherQueryAndTuple != null) {
                    //we have a key match ...
                    sharedSet.remove(otherQueryAndTuple.getValue());
                    final T otherRecord = otherQueryAndTuple.getValue();
                    if (!thisRecord.equalsNonKeys(otherRecord)) {
                        // ...and contents differ
                        result.addDifference(thisQueryAndTuple, otherQueryAndTuple);
                    }
                }
            }
        }
    }

    private class TupleMapperExt<T>  implements Runnable {
        private final Iterator<T> tupleIterator;
        private final Map<EqualityWrapper<T>, SourceAndTuple<T>> sharedMap;
        private final SynchronizedComparisonResult<T> result;
        private final ISourceCfg iSource;
        private final ExternalEquality<T> categorizer;
        private final ExternalEquality<T> equality;

        private TupleMapperExt(final Iterator<T> tupleIterator, final Map<EqualityWrapper<T>, SourceAndTuple<T>> sharedMap, final SynchronizedComparisonResult<T> result, ISourceCfg iSource, ExternalEquality<T> categorizer, ExternalEquality<T> equality) {
            this.tupleIterator = tupleIterator;
            this.sharedMap = sharedMap;
            this.result = result;
            this.iSource = iSource;
            this.categorizer = categorizer;
            this.equality = equality;
        }

        @Override
        public void run() {
            for (T thisRecord = getNextRecord(tupleIterator); thisRecord != null; thisRecord = getNextRecord(tupleIterator)) {
                SourceAndTuple<T> thisQueryAndTuple = new SourceAndTuple<>(iSource, thisRecord);
                EqualityWrapper<T> wrap = categorizer.wrap(thisRecord);
                SourceAndTuple<T> otherQueryAndTuple = sharedMap.putIfAbsent(wrap, thisQueryAndTuple);
                if (otherQueryAndTuple != null) {
                    //we have a key match ...
                    sharedMap.remove(wrap);
                    final T otherRecord = otherQueryAndTuple.getValue();
                    if (!equality.equals(thisRecord,otherRecord)) {
                        // ...and contents differ
                        result.addDifference(thisQueryAndTuple, otherQueryAndTuple);
                    }
                }
            }
        }
    }
}
