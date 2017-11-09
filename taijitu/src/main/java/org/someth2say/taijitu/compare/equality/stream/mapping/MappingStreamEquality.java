package org.someth2say.taijitu.compare.equality.stream.mapping;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.compare.equality.stream.AbstractStreamEquality;
import org.someth2say.taijitu.compare.equality.stream.StreamEquality;
import org.someth2say.taijitu.compare.equality.structure.StructureEquality;
import org.someth2say.taijitu.compare.equality.structure.StructureEqualityWrapper;
import org.someth2say.taijitu.compare.result.ComparisonResult;
import org.someth2say.taijitu.compare.result.ComparisonResult.SourceAndTuple;
import org.someth2say.taijitu.compare.result.SynchronizedComparisonResult;
import org.someth2say.taijitu.config.interfaces.ISourceCfg;
import org.someth2say.taijitu.config.interfaces.IStrategyCfg;
import org.someth2say.taijitu.source.Source;

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
public class MappingStreamEquality<T> extends AbstractStreamEquality<T> implements StreamEquality<T> {
    public static final String NAME = "mapping";
    private static final Logger logger = Logger.getLogger(MappingStreamEquality.class);

    protected MappingStreamEquality(StructureEquality<T> equality, StructureEquality<T> categorizer) {
        super(equality, categorizer);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public ComparisonResult<T> runExternalComparison(Source<T> source, Source<T> target) {
        SynchronizedComparisonResult<T> result = new SynchronizedComparisonResult<>();

        //1.- Build/run mapping tasks
        //TODO: Another option is running queries/pages alternating, so we can "restrict" memory usage, but only using a single thread
        final ExecutorService executorService = Executors.newFixedThreadPool(2);
        Map<StructureEqualityWrapper<T>, SourceAndTuple<T>> sharedMap = new ConcurrentHashMap<>();
        Runnable sourceMapper = new TupleMapperExt<>(source.iterator(), sharedMap, result, source.getConfig(), getCategorizer(), getEquality());
        Runnable targetMapper = new TupleMapperExt<>(target.iterator(), sharedMap, result, target.getConfig(), getCategorizer(), getEquality());
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

    private class TupleMapperExt<T>  implements Runnable {
        private final Iterator<T> tupleIterator;
        private final Map<StructureEqualityWrapper<T>, SourceAndTuple<T>> sharedMap;
        private final SynchronizedComparisonResult<T> result;
        private final ISourceCfg iSource;
        private final StructureEquality<T> categorizer;
        private final StructureEquality<T> equality;

        private TupleMapperExt(final Iterator<T> tupleIterator, final Map<StructureEqualityWrapper<T>, SourceAndTuple<T>> sharedMap, final SynchronizedComparisonResult<T> result, ISourceCfg iSource, StructureEquality<T> categorizer, StructureEquality<T> equality) {
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
                StructureEqualityWrapper<T> wrap = categorizer.wrap(thisRecord);
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
