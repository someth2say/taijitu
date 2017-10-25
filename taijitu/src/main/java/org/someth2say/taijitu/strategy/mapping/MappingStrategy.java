package org.someth2say.taijitu.strategy.mapping;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.ComparisonContext;
import org.someth2say.taijitu.compare.ComparisonResult;
import org.someth2say.taijitu.compare.ComparisonResult.QueryAndTuple;
import org.someth2say.taijitu.compare.SynchronizedComparisonResult;
import org.someth2say.taijitu.config.ComparisonConfig;
import org.someth2say.taijitu.config.QueryConfig;
import org.someth2say.taijitu.config.StrategyConfig;
import org.someth2say.taijitu.database.ResultSetIterator;
import org.someth2say.taijitu.strategy.AbstractComparisonStrategy;
import org.someth2say.taijitu.strategy.ComparisonStrategy;
import org.someth2say.taijitu.tuple.ComparableTuple;
import org.someth2say.taijitu.util.ImmutablePair;

import java.util.Collection;
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
    public ComparisonResult runComparison(ResultSetIterator source, ResultSetIterator target, ComparisonContext comparisonContext, ComparisonConfig comparisonConfig) {
        final String comparisonName = comparisonConfig.getName();
        logger.debug("Start mapping strategy comparison for " + comparisonName);
        SynchronizedComparisonResult result = new SynchronizedComparisonResult(comparisonConfig);

        //1.- Build/run mapping tasks
        //TODO: Consider splitting queries into "pages", so mapping can occur in more threads.
        //TODO: Another option is running queries/pages alternating, so we can "restrict" memory usage, but only using a single thread
        final ExecutorService executorService = Executors.newFixedThreadPool(2);
        Map<ComparableTuple, QueryAndTuple> sharedMap = new ConcurrentHashMap<>();
        Runnable sourceMapper = new Mapper(source, sharedMap, result, comparisonConfig.getSourceQueryConfig());
        Runnable targetMapper = new Mapper(target, sharedMap, result, comparisonConfig.getTargetQueryConfig());
        executorService.submit(sourceMapper);// Map source
        executorService.submit(targetMapper);// Map target

        shutdownAndAwaitTermination(executorService);

        //2.- When both mapping tasks are completed, remaining data are source/target only
        final Collection<QueryAndTuple> entries = sharedMap.values();
        result.getDisjoint().addAll(entries);

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

    public static StrategyConfig defaultConfig() {
        return () -> MappingStrategy.NAME;
    }

    private class Mapper implements Runnable {
        private final ResultSetIterator resultSetIterator;
        private final Map<ComparableTuple, ComparisonResult.QueryAndTuple> sharedSet;
        private final SynchronizedComparisonResult result;
        private final QueryConfig queryConfig;

        private <T extends ComparableTuple> Mapper(final ResultSetIterator resultSetIterator, final Map<ComparableTuple, ComparisonResult.QueryAndTuple> sharedMap, final SynchronizedComparisonResult result, QueryConfig queryConfig) {
            this.resultSetIterator = resultSetIterator;
            this.sharedSet = sharedMap;
            this.result = result;
            this.queryConfig = queryConfig;
        }

        @Override
        public void run() {
            for (ComparableTuple thisRecord = getNextRecord(resultSetIterator); thisRecord != null; thisRecord = getNextRecord(resultSetIterator)) {

                final QueryAndTuple otherQueryAndTuple = sharedSet.putIfAbsent(thisRecord, new QueryAndTuple(queryConfig, thisRecord));
                if (otherQueryAndTuple != null) {
                    //we have a key match ...
                    sharedSet.remove(otherQueryAndTuple.getValue());
                    final ComparableTuple otherRecord = otherQueryAndTuple.getValue();
                    if (!thisRecord.equalsNonKeys(otherRecord)) {
                        // ...and contents differ
                        result.getDifferent().add(new ImmutablePair<>(new QueryAndTuple(queryConfig, thisRecord), otherQueryAndTuple));
                    }
                }


            }
        }
    }

}
