package org.someth2say.taijitu.compare.equality.stream.mapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.compare.equality.composite.ICompositeEquality;
import org.someth2say.taijitu.compare.equality.stream.AbstractStreamEquality;
import org.someth2say.taijitu.compare.equality.stream.StreamEquality;
import org.someth2say.taijitu.compare.equality.composite.wrapper.CompositeEqualityWrapper;
import org.someth2say.taijitu.compare.result.ComparisonResult;
import org.someth2say.taijitu.compare.result.ComparisonResult.SourceIdAndComposite;
import org.someth2say.taijitu.compare.result.SynchronizedComparisonResult;
import org.someth2say.taijitu.config.interfaces.IStrategyCfg;
import org.someth2say.taijitu.discarter.TimeBiDiscarter;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Created by Jordi Sola on 02/03/2017.
 */
public class MappingStreamEquality<T> extends AbstractStreamEquality<T> implements StreamEquality<T> {
    public static final String NAME = "mapping";
    private static final Logger logger = LoggerFactory.getLogger(MappingStreamEquality.class);

    public MappingStreamEquality(ICompositeEquality<T> equality, ICompositeEquality<T> categorizer) {
        super(equality, categorizer);
    }

    @Override
    public String getName() {
        return NAME;
    }

    public ComparisonResult<T> runComparison(Stream<T> source, Object sourceId, Stream<T> target, Object targetId) {
        return compare(source.iterator(), sourceId, target.iterator(), targetId);
    }

//    @Override
//    public ComparisonResult<TMT> runExternalComparison(Source<TMT> source, Source<TMT> target) {
//        Iterator<TMT> sourceIterator = source.iterator();
//        Iterator<TMT> targetIterator = target.iterator();
//        Object sourceID = source.getConfig();
//        ISourceCfg targetId = target.getConfig();
//        return compare(sourceIterator, sourceID, targetIterator, targetId);
//    }

    private ComparisonResult<T> compare(Iterator<T> source, Object sourceID, Iterator<T> target, Object targetId) {
        //1.- Build/run mapping tasks
        SynchronizedComparisonResult<T> result = new SynchronizedComparisonResult<>();
        //TODO: Another option is running queries/pages alternating, so we can "restrict" memory usage, but only using a single thread
        final ExecutorService executorService = Executors.newFixedThreadPool(2);
        Map<CompositeEqualityWrapper<T>, SourceIdAndComposite<T>> sharedMap = new ConcurrentHashMap<>();
        Runnable sourceMapper = new TupleMapperExt<>(source, sharedMap, result, sourceID, getCategorizer(), getEquality());
        Runnable targetMapper = new TupleMapperExt<>(target, sharedMap, result, targetId, getCategorizer(), getEquality());
        executorService.submit(sourceMapper);// Map source
        executorService.submit(targetMapper);// Map target

        shutdownAndAwaitTermination(executorService);

        //2.- When both mapping tasks are completed, remaining data are source/target only
        final Collection<SourceIdAndComposite<T>> entries = sharedMap.values();
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

    private class TupleMapperExt<TMT> implements Runnable {
        private final Iterator<TMT> source;
        private final Map<CompositeEqualityWrapper<TMT>, SourceIdAndComposite<TMT>> sharedMap;
        private final SynchronizedComparisonResult<TMT> result;
        private final Object sourceId;
        private final ICompositeEquality<TMT> categorizer;
        private final ICompositeEquality<TMT> equality;

        private TupleMapperExt(final Iterator<TMT> source, final Map<CompositeEqualityWrapper<TMT>, SourceIdAndComposite<TMT>> sharedMap, final SynchronizedComparisonResult<TMT> result, Object sourceId, ICompositeEquality<TMT> categorizer, ICompositeEquality<TMT> equality) {
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
                SourceIdAndComposite<TMT> thisQueryAndTuple = new SourceIdAndComposite<>(sourceId, thisRecord);
                CompositeEqualityWrapper<TMT> wrap = categorizer.wrap(thisRecord);
                SourceIdAndComposite<TMT> otherQueryAndTuple = sharedMap.putIfAbsent(wrap, thisQueryAndTuple);
                if (otherQueryAndTuple != null) {
                    //we have a key match ...
                    sharedMap.remove(wrap);
                    final TMT otherRecord = otherQueryAndTuple.getComposite();
                    if (!equality.equals(thisRecord, otherRecord)) {
                        // ...and contents differ
                        result.addDifference(thisQueryAndTuple, otherQueryAndTuple);
                    }
                }
                timedLogger.accept("Processed {} records from source {}", new Object[]{recordCount, sourceId});

                thisRecord = getNextRecordOrNull(source);
            }
        }
    }
}
