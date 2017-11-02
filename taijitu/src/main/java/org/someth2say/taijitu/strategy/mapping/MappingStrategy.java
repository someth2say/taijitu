package org.someth2say.taijitu.strategy.mapping;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.ComparisonContext;
import org.someth2say.taijitu.compare.ComparisonResult;
import org.someth2say.taijitu.compare.ComparisonResult.QueryAndTuple;
import org.someth2say.taijitu.compare.SynchronizedComparisonResult;
import org.someth2say.taijitu.config.interfaces.IComparisonCfg;
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
    public ComparisonResult runComparison(Source source, Source target, ComparisonContext comparisonContext, IComparisonCfg comparisonConfig) {
        final String comparisonName = comparisonConfig.getName();
        logger.debug("Start mapping strategy comparison for " + comparisonName);
        SynchronizedComparisonResult result = new SynchronizedComparisonResult(comparisonConfig);

        //1.- Build/run mapping tasks
        //TODO: Another option is running queries/pages alternating, so we can "restrict" memory usage, but only using a single thread
        final ExecutorService executorService = Executors.newFixedThreadPool(2);
        Map<ComparableTuple, QueryAndTuple> sharedMap = new ConcurrentHashMap<>();
        Runnable sourceMapper = new TupleMapper(source.iterator(), sharedMap, result, source.getConfig());
        Runnable targetMapper = new TupleMapper(target.iterator(), sharedMap, result, target.getConfig());

        executorService.submit(sourceMapper);// Map source
        executorService.submit(targetMapper);// Map target

        shutdownAndAwaitTermination(executorService);

        //2.- When both mapping tasks are completed, remaining data are source/target only
        final Collection<QueryAndTuple> entries = sharedMap.values();
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

    private class TupleMapper implements Runnable {
        private final Iterator<ComparableTuple> tupleIterator;
        private final Map<ComparableTuple, ComparisonResult.QueryAndTuple> sharedSet;
        private final SynchronizedComparisonResult result;
        private final ISourceCfg iSource;

        private TupleMapper(final Iterator<ComparableTuple> tupleIterator, final Map<ComparableTuple, QueryAndTuple> sharedMap, final SynchronizedComparisonResult result, ISourceCfg iSource) {
            this.tupleIterator = tupleIterator;
            this.sharedSet = sharedMap;
            this.result = result;
            this.iSource = iSource;
        }

        @Override
        public void run() {
            for (ComparableTuple thisRecord = getNextRecord(tupleIterator); thisRecord != null; thisRecord = getNextRecord(tupleIterator)) {

                QueryAndTuple thisQueryAndTuple = new QueryAndTuple(iSource, thisRecord);
                final QueryAndTuple otherQueryAndTuple = sharedSet.putIfAbsent(thisRecord, thisQueryAndTuple);
                if (otherQueryAndTuple != null) {
                    //we have a key match ...
                    sharedSet.remove(otherQueryAndTuple.getValue());
                    final ComparableTuple otherRecord = otherQueryAndTuple.getValue();
                    if (!thisRecord.equalsNonKeys(otherRecord)) {
                        // ...and contents differ
                        result.addDifference(thisQueryAndTuple, otherQueryAndTuple);
                    }
                }


            }
        }
    }

}
