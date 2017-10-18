package org.someth2say.taijitu.strategy.mapping;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.ComparisonRuntime;
import org.someth2say.taijitu.compare.ComparisonResult;
import org.someth2say.taijitu.compare.SynchronizedComparisonResult;
import org.someth2say.taijitu.tuple.ComparableTuple;
import org.someth2say.taijitu.config.ComparisonConfig;
import org.someth2say.taijitu.config.StrategyConfig;
import org.someth2say.taijitu.database.ResultSetIterator;
import org.someth2say.taijitu.strategy.AbstractComparisonStrategy;
import org.someth2say.taijitu.strategy.ComparisonStrategy;
import org.someth2say.taijitu.util.ImmutablePair;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

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
    public <T extends ComparableTuple> ComparisonResult runComparison(ResultSetIterator<T> source, ResultSetIterator<T> target, ComparisonRuntime comparisonRuntime, ComparisonConfig comparisonConfig) {
        final String comparisonName = comparisonConfig.getName();
        logger.debug("Start mapping strategy comparison for " + comparisonName);
        SynchronizedComparisonResult result = new SynchronizedComparisonResult(comparisonConfig);

        //1.- Build/run mapping tasks
        //TODO: Consider splitting queries into "pages", so mapping can occur in more threads.
        final ExecutorService executorService = Executors.newFixedThreadPool(2);
        Map<ComparableTuple, ComparableTuple> sharedMap = new ConcurrentHashMap<>();
        Runnable sourceMapper = new Mapper(source, sharedMap, result);
        Runnable targetMapper = new Mapper(target, sharedMap, result);
        executorService.submit(sourceMapper);// Map source
        executorService.submit(targetMapper);// Map target

        shutdownAndAwaitTermination(executorService);

        //2.- When both mapping tasks are completed, remaining data are source/target only
        final Set<Map.Entry<ComparableTuple, ComparableTuple>> entries = sharedMap.entrySet();
        for (Map.Entry<ComparableTuple, ComparableTuple> entry : entries) {
            result.getSourceOnly().add(entry.getKey());
            result.getTargetOnly().add(entry.getKey());
        }

        return result;
    }

    void shutdownAndAwaitTermination(ExecutorService pool) {
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
        private final ResultSetIterator<? extends ComparableTuple> resultSetIterator;
        private final Map<ComparableTuple, ComparableTuple> sharedSet;
        private final SynchronizedComparisonResult result;

        private <T extends ComparableTuple> Mapper(final ResultSetIterator<T> resultSetIterator, final Map<ComparableTuple, ComparableTuple> sharedMap, final SynchronizedComparisonResult result) {
            this.resultSetIterator = resultSetIterator;
            this.sharedSet = sharedMap;
            this.result = result;
        }

        @Override
        public void run() {
            for (ComparableTuple nextRecord = getNextRecord(resultSetIterator); nextRecord != null; nextRecord = getNextRecord(resultSetIterator)) {
                final ComparableTuple otherRecord = sharedSet.putIfAbsent(nextRecord, nextRecord);
                if (otherRecord != null) {
                    //we have a key match ...
                    if (!nextRecord.equalsNonKeys(otherRecord)) {
                        // ...and contents differ
                        //TODO: This does not ensure witch record came from witch query...
                        result.getDifferent().add(new ImmutablePair<>(nextRecord, otherRecord));
                    }
                }
            }
        }
    }
}
