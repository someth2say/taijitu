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
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.someth2say.taijitu.compare.equality.aspects.external.Hasher;
import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.compare.equality.impl.stream.StreamEqualizer;
import org.someth2say.taijitu.compare.equality.wrapper.IHashableWraper;
import org.someth2say.taijitu.compare.result.Difference;
import org.someth2say.taijitu.compare.result.Unequal;
import org.someth2say.taijitu.compare.result.Missing;
import org.someth2say.taijitu.util.StreamUtil;

/**
 * Created by Jordi Sola on 02/03/2017.
 */
public class HashingStreamEqualizer<T> implements StreamEqualizer<T> {

    final private Equalizer<T> equalizer;
    final private Hasher<T> hasher;

    private boolean parallel = false;

    public HashingStreamEqualizer(Equalizer<T> equalizer, Hasher<T> hasher) {
        this.equalizer = equalizer;
        this.hasher = hasher;
    }

    @Override
    public Stream<Difference<?>> underlyingDiffs(Stream<T> source, Stream<T> target) {
        if (parallel)
            return matchParallel(source, target, hasher, equalizer);
        else
            return matchSequential(source, target, hasher, equalizer);
    }

    private static <T> Stream<Difference<?>> matchSequential(Stream<T> source, Stream<T> target, Hasher<T> categorizer, Equalizer<T> equalizer) {

        Map<IHashableWraper<T, ?>, OrdinalAndComposite<T>> sharedMap = getSharedMap();

        //TODO: This exploits a side effect of manipulating input (filling the map). Should find a different way.
        Stream<Unequal<T>> differences = StreamUtil
                .zip(source.map(c -> new OrdinalAndComposite<>(0, c)),
                        target.map(c -> new OrdinalAndComposite<>(1, c)), true)
                .map(sac -> Mapper.map(sac, categorizer, sharedMap, equalizer))
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

        //TODO: Make it actually a lazy stream
        return result.stream();
    }

    private static <T> Map<IHashableWraper<T, ?>, OrdinalAndComposite<T>> getSharedMap() {
        return new ConcurrentHashMap<>();
    }

    public static <T> Stream<Difference<?>> matchParallel(Stream<T> source, Stream<T> target,
                                                          Hasher<T> hasher, Equalizer<T> equalizer) {
        Map<IHashableWraper<T, ?>, OrdinalAndComposite<T>> sharedMap = getSharedMap();

//        return newWay(source, target, hasher, equalizer, sharedMap);

        return oldWay(source, target, hasher, equalizer, sharedMap);

    }

    public static <T> Stream<Difference<?>> newWay(Stream<T> source, Stream<T> target, Hasher<T> hasher, Equalizer<T> equalizer, Map<IHashableWraper<T, ?>, OrdinalAndComposite<T>> sharedMap) {
        Stream<Unequal<T>> unequals = StreamUtil.zip(source.map(t -> new OrdinalAndComposite<>(1, t)), target.map(t -> new OrdinalAndComposite<>(2, t)), 1, true)
                .map(oac -> Mapper.map(oac, hasher, sharedMap, equalizer)).filter(Objects::nonNull);

        Stream<Missing<T>> missing = sharedMap.values().stream().map(oac -> hasher.asMissing(oac.getComposite()));

        return Stream.concat(unequals, missing);
    }

    public static <T> Stream<Difference<?>> oldWay(Stream<T> source, Stream<T> target, Hasher<T> hasher, Equalizer<T> equalizer, Map<IHashableWraper<T, ?>, OrdinalAndComposite<T>> sharedMap) {
        List<Difference<?>> result = Collections.synchronizedList(new ArrayList<>());
        // 1.- Build/run mapping tasks
        Iterator<T> sourceIt = source.iterator();
        Iterator<T> targetIt = target.iterator();

        final ExecutorService executorService = Executors.newFixedThreadPool(2);
        Runnable sourceMapper = new Mapper<>(sourceIt, sharedMap, result, 0, hasher, equalizer);
        Runnable targetMapper = new Mapper<>(targetIt, sharedMap, result, 1, hasher, equalizer);

        executorService.submit(sourceMapper);// Map source
        executorService.submit(targetMapper);// Map target

        shutdownAndAwaitTermination(executorService);

// 2.- When both mapping tasks are completed, remaining data are source/target
// only
        final Collection<OrdinalAndComposite<T>> entries = sharedMap.values();
        entries.stream().forEach(sc -> result.add(hasher.asMissing(sc.getComposite())));

        //TODO: Make it actually a lazy stream
        return result.stream();
    }

    private static void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            while (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow();
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    public void setParallel(boolean parallel) {
        this.parallel = parallel;
    }
}
