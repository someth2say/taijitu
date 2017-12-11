package org.someth2say.taijitu.compare.equality.impl.stream.mapping;

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
import org.someth2say.taijitu.compare.equality.aspects.external.Hasher;
import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.compare.equality.aspects.internal.Hashable;
import org.someth2say.taijitu.compare.equality.impl.stream.StreamEqualizer;
import org.someth2say.taijitu.compare.result.Difference;
import org.someth2say.taijitu.compare.result.Unequal;
import org.someth2say.taijitu.compare.result.Missing;
import org.someth2say.taijitu.discarter.TimeBiDiscarter;
import org.someth2say.taijitu.util.StreamUtil;

/**
 * Created by Jordi Sola on 02/03/2017.
 */
public class HashingStreamEqualizer<T> implements StreamEqualizer<T> {

    private static final Logger logger = LoggerFactory.getLogger(HashingStreamEqualizer.class);
    final private Equalizer<T> equalizer;
    final private Hasher<T> categorizer;

    public HashingStreamEqualizer(Equalizer<T> equalizer, Hasher<T> categorizer) {
        this.equalizer = equalizer;
        this.categorizer = categorizer;
    }

    @Override
    public List<Difference<?>> underlyingDiffs(Stream<T> source, Stream<T> target) {
        // TODO: Find a way to discriminate (config)?
        // return matchParallel(source, sourceID, target, targetId, categorizer, equalizer);
        return matchSequential(source, target, categorizer, equalizer);
    }

    private static <T> List<Difference<?>> matchSequential(Stream<T> source, Stream<T> target, Hasher<T> categorizer, Equalizer<T> equalizer) {

        Map<Hashable<T>, OrdinalAndComposite<T>> sharedMap = new ConcurrentHashMap<>();
        final int recordCount = 0;
        final TimeBiDiscarter<String, Object[]> timedLogger = new TimeBiDiscarter<>(1000, logger::debug);

        //TODO: This exploits a side effect of manipulating input (filling the map). Should find a different way.
        Stream<Unequal<T>> differences = StreamUtil
                .zip(source.map(c -> new OrdinalAndComposite<>(0, c)),
                        target.map(c -> new OrdinalAndComposite<>(1, c)))
                .map(sac -> map(sac, timedLogger, recordCount, categorizer, sharedMap, equalizer))
                .filter(Objects::nonNull);
        List<Unequal<T>> diffs = differences.collect(Collectors.toList());

        // 2.- When both mapping tasks are completed, remaining data are source/target
        // only
        Stream<Missing<T>> missings = sharedMap.values().stream()
                .map(sac -> categorizer.asMissing(sac.getComposite()));

        List<Missing<T>> miss = missings.collect(Collectors.toList());
        List<Difference<?>> result = new ArrayList<>(diffs.size() + miss.size());
        result.addAll(diffs);
        result.addAll(miss);
        return result;
    }


    private static <T> Unequal<T> map(T composite, int ordinal, TimeBiDiscarter<String, Object[]> timedLogger, int recordCount, Hasher<T> mapper, Map<Hashable<T>, OrdinalAndComposite<T>> sharedMap, Equalizer<T> equalizer) {
        OrdinalAndComposite<T> oac = new OrdinalAndComposite<>(ordinal, composite);
        return map(oac, timedLogger, recordCount, mapper, sharedMap, equalizer);
    }

    private static <T> Unequal<T> map(OrdinalAndComposite<T> thisOaC,
                                      TimeBiDiscarter<String, Object[]> timedLogger, int recordCount, Hasher<T> categorizer,
                                      Map<Hashable<T>, OrdinalAndComposite<T>> sharedMap, Equalizer<T> equalizer) {
        timedLogger.accept("Processed {} records so far.", new Object[]{recordCount});
        Hashable<T> wraped = categorizer.wrap(thisOaC.getComposite());
        OrdinalAndComposite<T> otherOaC = sharedMap.putIfAbsent(wraped, new OrdinalAndComposite<>(thisOaC.getOrdinal(), thisOaC.getComposite()));
        if (otherOaC != null) {
            // we have a key match ...
            sharedMap.remove(wraped);
            return getUnequal(equalizer, thisOaC, otherOaC);
        }
        return null;
    }

    private static <T> Unequal<T> getUnequal(Equalizer<T> equalizer, OrdinalAndComposite<T> first, OrdinalAndComposite<T> second) {
        Unequal<T> unequal;
        if (first.getOrdinal() < second.getOrdinal()) {
            unequal = equalizer.asDifference(first.getComposite(), second.getComposite());
        } else {
            unequal = equalizer.asDifference(second.getComposite(), first.getComposite());
        }
        return unequal;
    }

    public static <T> List<Difference<?>> matchParallel(Stream<T> source, Stream<T> target,
                                                        Hasher<T> categorizer, Equalizer<T> equalizer) {
        // 1.- Build/run mapping tasks
        List<Difference<?>> result = Collections.synchronizedList(new ArrayList<>());
        Iterator<T> sourceIt = source.iterator();
        Iterator<T> targetIt = target.iterator();

        final ExecutorService executorService = Executors.newFixedThreadPool(2);
        Map<Hashable<T>, OrdinalAndComposite<T>> sharedMap = new ConcurrentHashMap<>();
        Runnable sourceMapper = new TupleMapperExt<>(sourceIt, sharedMap, result, 0, categorizer, equalizer);
        Runnable targetMapper = new TupleMapperExt<>(targetIt, sharedMap, result, 1, categorizer, equalizer);

        executorService.submit(sourceMapper);// Map source
        executorService.submit(targetMapper);// Map target

        shutdownAndAwaitTermination(executorService);

// 2.- When both mapping tasks are completed, remaining data are source/target
// only
        final Collection<OrdinalAndComposite<T>> entries = sharedMap.values();
        entries.stream().forEach(sc -> result.add(categorizer.asMissing(sc.getComposite())));

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
        private final Map<Hashable<TMT>, OrdinalAndComposite<TMT>> sharedMap;
        private final List<Difference<?>> result;
        private final int ordinal;
        private final Hasher<TMT> categorizer;
        private final Equalizer<TMT> equalizer;

        private TupleMapperExt(final Iterator<TMT> source,
                               final Map<Hashable<TMT>, OrdinalAndComposite<TMT>> sharedMap,
                               final List<Difference<?>> result, int ordinal, Hasher<TMT> categorizer,
                               Equalizer<TMT> equalizer) {
            this.source = source;
            this.sharedMap = sharedMap;
            this.result = result;
            this.ordinal = ordinal;
            this.categorizer = categorizer;
            this.equalizer = equalizer;
        }

        @Override
        public void run() {
            TimeBiDiscarter<String, Object[]> timedLogger = new TimeBiDiscarter<>(1000, logger::debug);
            int recordCount = 0;
            TMT thisRecord = getNextRecordOrNull(source);
            while (thisRecord != null) {
                recordCount++;
                Unequal<TMT> unequal = map(thisRecord, ordinal, timedLogger, recordCount, categorizer, sharedMap, equalizer);
                if (unequal != null) {
                    result.add(unequal);
                }
                thisRecord = getNextRecordOrNull(source);
            }
        }

    }

    private static <T> T getNextRecordOrNull(Iterator<T> resultSetSource) {
        return resultSetSource.hasNext() ? resultSetSource.next() : null;
    }
}
