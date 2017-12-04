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
import org.someth2say.taijitu.compare.equality.aspects.external.HasherEqualizer;
import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.compare.equality.aspects.internal.CategorizableEqualizable;
import org.someth2say.taijitu.compare.equality.impl.stream.StreamEqualizer;
import org.someth2say.taijitu.compare.result.Difference;
import org.someth2say.taijitu.compare.result.Mismatch;
import org.someth2say.taijitu.compare.result.Missing;
import org.someth2say.taijitu.discarter.TimeBiDiscarter;
import org.someth2say.taijitu.util.StreamUtil;

/**
 * Created by Jordi Sola on 02/03/2017.
 */
public class MappingStreamEqualizer<T> implements StreamEqualizer<T> {

    private static final Logger logger = LoggerFactory.getLogger(MappingStreamEqualizer.class);
    final private Equalizer<T> equalizer;
    final private HasherEqualizer<T> categorizer;

    public MappingStreamEqualizer(Equalizer<T> equalizer, HasherEqualizer<T> categorizer) {
        this.equalizer = equalizer;
        this.categorizer = categorizer;
    }

    @Override
    public List<Mismatch<?>> underlyingDiffs(Stream<T> source, Stream<T> target) {
        // TODO: Find a way to discriminate (config)?
        // return matchParallel(source, sourceID, target, targetId, categorizer, equalizer);
        return matchSequential(source, target, categorizer, equalizer);
    }

    private static <T> List<Mismatch<?>> matchSequential(Stream<T> source, Stream<T> target, HasherEqualizer<T> categorizer, Equalizer<T> equalizer) {

        Map<CategorizableEqualizable<T>, OrdinalAndComposite<T>> sharedMap = new ConcurrentHashMap<>();
        final int recordCount = 0;
        final TimeBiDiscarter<String, Object[]> timedLogger = new TimeBiDiscarter<>(1000, logger::debug);

        Stream<Difference<T>> differences = StreamUtil
                .zip(source.map(c -> new OrdinalAndComposite<>(0, c)),
                        target.map(c -> new OrdinalAndComposite<>(1, c)))
                .map(sac -> map(sac.getComposite(), sac.getOrdinal(), timedLogger, recordCount, categorizer, sharedMap,
                        equalizer))
                .filter(Objects::nonNull);

        // 2.- When both mapping tasks are completed, remaining data are source/target
        // only
        Stream<Missing<T>> missings = sharedMap.values().stream()
                .map(sac -> categorizer.asMissing(sac.getComposite()));

        return Stream.concat(differences, missings).collect(Collectors.toList());
    }

    private static <T> Difference<T> map(T thisComposite, int thisOrdinal,
                                         TimeBiDiscarter<String, Object[]> timedLogger, int recordCount, HasherEqualizer<T> categorizer,
                                         Map<CategorizableEqualizable<T>, OrdinalAndComposite<T>> sharedMap, Equalizer<T> equalizer) {
        timedLogger.accept("Processed {} records so far.", new Object[]{recordCount});
        CategorizableEqualizable<T> wrap = categorizer.wrap(thisComposite);
        OrdinalAndComposite<T> otherSaC = sharedMap.putIfAbsent(wrap, new OrdinalAndComposite<>(thisOrdinal, thisComposite));
        if (otherSaC != null) {
            // we have a key matchSequential ...
            sharedMap.remove(wrap);
            final T otherComposite = otherSaC.getComposite();
            List<Mismatch<?>> differences = equalizer.underlyingDiffs(thisComposite, otherComposite);
            if (differences != null && !differences.isEmpty()) {
                // ...and contents differ
                if (thisOrdinal < otherSaC.getOrdinal())

                    return new Difference<>(equalizer, thisComposite, otherComposite, differences);
                else
                    return new Difference<>(equalizer, otherComposite, thisComposite, differences);
            }
        }
        return null;
    }

    public static <T> List<Mismatch<?>> matchParallel(Stream<T> source, Object sourceID, Stream<T> target, Object targetId,
                                                      HasherEqualizer<T> categorizer, Equalizer<T> equalizer) {
        // 1.- Build/run mapping tasks
        List<Mismatch<?>> result = Collections.synchronizedList(new ArrayList<>());
        Iterator<T> sourceIt = source.iterator();
        Iterator<T> targetIt = target.iterator();

        final ExecutorService executorService = Executors.newFixedThreadPool(2);
        Map<CategorizableEqualizable<T>, OrdinalAndComposite<T>> sharedMap = new ConcurrentHashMap<>();
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
        private final Map<CategorizableEqualizable<TMT>, OrdinalAndComposite<TMT>> sharedMap;
        private final List<Mismatch<?>> result;
        private final int ordinal;
        private final HasherEqualizer<TMT> categorizer;
        private final Equalizer<TMT> equalizer;

        private TupleMapperExt(final Iterator<TMT> source,
                               final Map<CategorizableEqualizable<TMT>, OrdinalAndComposite<TMT>> sharedMap,
                               final List<Mismatch<?>> result, int ordinal, HasherEqualizer<TMT> categorizer,
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
                Difference<TMT> difference = map(thisRecord, ordinal, timedLogger, recordCount, categorizer, sharedMap, equalizer);
                if (difference != null) {
                    result.add(difference);
                }
                thisRecord = getNextRecordOrNull(source);
            }
        }
    }

    private static <T> T getNextRecordOrNull(Iterator<T> resultSetSource) {
        return resultSetSource.hasNext() ? resultSetSource.next() : null;
    }
}
