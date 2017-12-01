package org.someth2say.taijitu.compare.equality.stream.mapping;

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
import org.someth2say.taijitu.compare.equality.external.CategorizerEquality;
import org.someth2say.taijitu.compare.equality.external.Equality;
import org.someth2say.taijitu.compare.equality.internal.CategorizableEqualizable;
import org.someth2say.taijitu.compare.equality.stream.AbstractStreamEquality;
import org.someth2say.taijitu.compare.equality.stream.MismatchHelper;
import org.someth2say.taijitu.compare.equality.stream.StreamEquality;
import org.someth2say.taijitu.compare.equality.wrapper.CategorizerEqualityWrapper;
import org.someth2say.taijitu.compare.result.Difference;
import org.someth2say.taijitu.compare.result.Mismatch;
import org.someth2say.taijitu.compare.result.Missing;
import org.someth2say.taijitu.discarter.TimeBiDiscarter;
import org.someth2say.taijitu.util.StreamUtil;

/**
 * Created by Jordi Sola on 02/03/2017.
 */
public class MappingStreamEquality<T> extends AbstractStreamEquality<T, CategorizerEquality<T>> implements StreamEquality<T> {

    private static final Logger logger = LoggerFactory.getLogger(MappingStreamEquality.class);

    public MappingStreamEquality(Equality<T> equality, CategorizerEquality<T> categorizer) {
        super(equality, categorizer);
    }

    @Override
    public List<Mismatch<?>> underlyingDiffs(Stream<T> source, Stream<T> target) {
        // TODO: Find a way to discriminate (config)?
        // return matchParallel(source, sourceID, target, targetId, getCategorizer(), getEquality());
        return matchSequential(source, target, getOther(), getEquality());
    }

    public static <T> List<Mismatch<?>> matchSequential(Stream<T> source, Stream<T> target, CategorizerEquality<T> categorizer, Equality<T> equality) {

        Map<CategorizableEqualizable<T, ? extends CategorizerEquality<T>>, OrdinalAndComposite<T>> sharedMap = new ConcurrentHashMap<>();
        final int recordCount = 0;
        final TimeBiDiscarter<String, Object[]> timedLogger = new TimeBiDiscarter<>(1000, logger::debug);

        Stream<Difference<T>> differences = StreamUtil
                .zip(source.map(c -> new OrdinalAndComposite<>(0, c)),
                        target.map(c -> new OrdinalAndComposite<>(1, c)))
                .map(sac -> map(sac.getComposite(), sac.getOrdinal(), timedLogger, recordCount, categorizer, sharedMap,
                        equality))
                .filter(Objects::nonNull);

        // 2.- When both mapping tasks are completed, remaining data are source/target
        // only
        Stream<Missing<T>> missings = sharedMap.values().stream()
                .map((OrdinalAndComposite<T> sac) -> new Missing<T>(categorizer, sac.getComposite()));

        return Stream.concat(differences, missings).collect(Collectors.toList());
    }

    private static <T> Difference<T> map(T thisComposite, int thisOrdinal,
                                         TimeBiDiscarter<String, Object[]> timedLogger, int recordCount, CategorizerEquality<T> categorizer,
                                         Map<CategorizableEqualizable<T, ? extends CategorizerEquality<T>>, OrdinalAndComposite<T>> sharedMap, Equality<T> equality) {
        timedLogger.accept("Processed {} records so far.", new Object[]{recordCount});
        CategorizableEqualizable<T, ? extends CategorizerEquality<T>> wrap = categorizer.wrap(thisComposite);
        OrdinalAndComposite<T> otherSaC = sharedMap.putIfAbsent(wrap, new OrdinalAndComposite<>(thisOrdinal, thisComposite));
        if (otherSaC != null) {
            // we have a key matchSequential ...
            sharedMap.remove(wrap);
            final T otherComposite = otherSaC.getComposite();
            List<Mismatch<?>> differences = equality.underlyingDiffs(thisComposite, otherComposite);
            if (differences != null && !differences.isEmpty()) {
                // ...and contents differ
                if (thisOrdinal < otherSaC.getOrdinal())
                    return new Difference<>(equality, thisComposite, otherComposite, differences);
                else
                    return new Difference<>(equality, otherComposite, thisComposite, differences);
            }
        }
        return null;
    }

    public static <T> List<Mismatch<?>> matchParallel(Stream<T> source, Object sourceID, Stream<T> target, Object targetId,
                                                      CategorizerEquality<T> categorizer, Equality<T> equality) {
        // 1.- Build/run mapping tasks
        List<Mismatch<?>> result = Collections.synchronizedList(new ArrayList<>());
        Iterator<T> sourceIt = source.iterator();
        Iterator<T> targetIt = target.iterator();

        final ExecutorService executorService = Executors.newFixedThreadPool(2);
        Map<CategorizableEqualizable<T, ? extends CategorizerEquality<T>>, OrdinalAndComposite<T>> sharedMap = new ConcurrentHashMap<>();
        Runnable sourceMapper = new TupleMapperExt<>(sourceIt, sharedMap, result, 0, categorizer, equality);
        Runnable targetMapper = new TupleMapperExt<>(targetIt, sharedMap, result, 1, categorizer, equality);

        executorService.submit(sourceMapper);// Map source
        executorService.submit(targetMapper);// Map target

        shutdownAndAwaitTermination(executorService);

        // 2.- When both mapping tasks are completed, remaining data are source/target
        // only
        final Collection<OrdinalAndComposite<T>> entries = sharedMap.values();
        entries.stream().forEach(sc -> MismatchHelper.addMissing(result, categorizer, sc.getComposite()));

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
        private final Map<CategorizableEqualizable<TMT, ? extends CategorizerEquality<TMT>>, OrdinalAndComposite<TMT>> sharedMap;
        private final List<Mismatch<?>> result;
        private final int ordinal;
        private final CategorizerEquality<TMT> categorizer;
        private final Equality<TMT> equality;

        private TupleMapperExt(final Iterator<TMT> source,
                               final Map<CategorizableEqualizable<TMT, ? extends CategorizerEquality<TMT>>, OrdinalAndComposite<TMT>> sharedMap,
                               final List<Mismatch<?>> result, int ordinal, CategorizerEquality<TMT> categorizer,
                               Equality<TMT> equality) {
            this.source = source;
            this.sharedMap = sharedMap;
            this.result = result;
            this.ordinal = ordinal;
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
                Difference<TMT> difference = map(thisRecord, ordinal, timedLogger, recordCount, categorizer, sharedMap, equality);
                if (difference != null) {
                    result.add(difference);
                }
                thisRecord = getNextRecordOrNull(source);
            }
        }
    }
}
