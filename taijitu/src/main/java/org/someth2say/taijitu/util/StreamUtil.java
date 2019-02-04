package org.someth2say.taijitu.util;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Inspired by https://stackoverflow.com/questions/17640754/zipping-streams-using-jdk8-with-lambda-java-util-stream-streams-zip
 * Contains several utilities for merging two different streams into a single one.
 */
public class StreamUtil {

    /**
     * `zip` method, with `batchSize` defaulted to `1`
     *
     * @param <C>
     * @param <A>
     * @param <B>
     * @param a
     * @param b
     * @param keepTail
     * @return
     */
    public static <C, A extends C, B extends C> Stream<C> zip(Stream<? extends A> a, Stream<? extends B> b, boolean keepTail) {
        return zip(a, b, 1, keepTail);
    }

    /**
     * Given two streams, produces a new stream that contains alternatively elements from first and second streams.
     * That is, given a `batchSize` of N, the resulting stream will produce, in order, first N elements from first stream.
     * Then, first N elements from second stream, then, following N elements from fists stream, and so on until ANY stream is deplete.
     * Finally, the remaining elements for the other stream will be provided or not based on 'keepTail' parameter.
     *  <pre>
     *     Stream<String> s1 = Stream.of("a", "b", "c");
     *     Stream<String> s2 = Stream.of("1", "2", "3", "4");
     *     Stream<String> zip = zip(s1, s2, 2);
     *     assertEquals(Arrays.asList("a", "b", "1", "2", "c"), zip.collect(Collectors.toList()));
     * </pre>
     *
     * @param <C>
     * @param <A>
     * @param <B>
     * @param first
     * @param second
     * @param batchSize
     * @param keepTail
     * @return
     */
    public static <C, A extends C, B extends C> Stream<C> zip(Stream<? extends A> first, Stream<? extends B> second, int batchSize, boolean keepTail) {
        Spliterator<? extends A> aSpliterator = Objects.requireNonNull(first).spliterator();
        Spliterator<? extends B> bSpliterator = Objects.requireNonNull(second).spliterator();

        Iterator<A> aIterator = Spliterators.iterator(aSpliterator);
        Iterator<B> bIterator = Spliterators.iterator(bSpliterator);

        Iterator<C> zipIterator = new ZipIterator<>(aIterator, bIterator, batchSize, keepTail);
        boolean parallel = first.isParallel() || second.isParallel();
        int lostCharacteristics = Spliterator.DISTINCT | Spliterator.SORTED | Spliterator.SIZED;
        BinaryOperator<Long> sizeOperator = null;
        // SIZE characteristic is only retained both streams are sized,
        if (aSpliterator.hasCharacteristics(Spliterator.SIZED) && bSpliterator.hasCharacteristics(Spliterator.SIZED)) { // A
            // Result stream size will be the common batchSize for both streams, and up to one more if available in first stream.
            lostCharacteristics = Spliterator.DISTINCT | Spliterator.SORTED;
            sizeOperator = (aSize, bSize) -> Math.min(aSize % batchSize, bSize % batchSize) + aSize > bSize ? Math.min(aSize - bSize, batchSize) : 0;
        }

        return getStreamFromIterators(aSpliterator, bSpliterator, zipIterator, sizeOperator, parallel, lostCharacteristics);
    }

    /**
     * Given two streams, apply `mapper` function to each element of both streams, in the order they are produced.
     * Resulting stream will be over when either input stream can produce no more elements.
     * <pre>
     *    Stream<String> s1 = of("a", "b", "c", "d");
     *    Stream<String> s2 = of("1", "2", "3");
     *    Stream<String> zip = biMap(s1, s2, String::concat);
     *    assertEquals(Arrays.asList("a1", "b2", "c3"), zip.collect(toList()));
     * </pre>
     * User should be aware of using unordered streams (streams that can produce elements in any order), as applying `biMap` on then can be non-deterministic.
     *
     * @param first
     * @param second
     * @param mapper
     * @param <A>
     * @param <B>
     * @param <C>
     * @return
     */
    public static <A, B, C> Stream<C> biMap(Stream<? extends A> first, Stream<? extends B> second,
                                            BiFunction<? super A, ? super B, ? extends C> mapper) {
        Objects.requireNonNull(mapper);
        Spliterator<? extends A> aSpliterator = Objects.requireNonNull(first).spliterator();
        Spliterator<? extends B> bSpliterator = Objects.requireNonNull(second).spliterator();

        Iterator<A> aIterator = Spliterators.iterator(aSpliterator);
        Iterator<B> bIterator = Spliterators.iterator(bSpliterator);
        Iterator<C> cIterator = new BiMapIterator<>(aIterator, bIterator, mapper);
        boolean parallel = first.isParallel() || second.isParallel();
        int lostCharacteristics = Spliterator.DISTINCT | Spliterator.SORTED;
        BinaryOperator<Long> sizeOperator = Math::min;
        return getStreamFromIterators(aSpliterator, bSpliterator, cIterator, sizeOperator, parallel, lostCharacteristics);

    }

    /**
     * A `biMapTail`version that applies the same `tailer` function to both streams.
     *
     * @param first
     * @param second
     * @param mapper
     * @param tailer
     * @param <A>
     * @param <C>
     * @return
     */
    public static <A, C> Stream<C> biMapTail(Stream<? extends A> first, Stream<? extends A> second,
                                             BiFunction<? super A, ? super A, ? extends C> mapper, Function<? super A, ? extends C> tailer) {
        return biMapTail(first, second, mapper, tailer, tailer);
    }

    /**
     * Like 'biMap', produces the stream resulting from applying the 'mapper' function to each pair (ordered) of elements for input streams.
     * The only (!) difference is that the resulting stream will not end when either stream depletes, but then it will produce the remaining elements, mapped by the `tailer` function.
     * <pre>
     *    Stream<String> s1 = of("a", "b", "c", "d");
     *    Stream<String> s2 = of("1", "2", "3");
     *    Stream<String> zip = biMapTail(s1, s2, String::concat, String::toUpperCase, identity());
     *    assertEquals(Arrays.asList("a1", "b2", "c3", "D"), zip.collect(toList()));
     * </pre>
     * Note that two `tailer` functions may be provided, one for elements of each stream.
     *
     * @param first
     * @param second
     * @param mapper
     * @param aTailer
     * @param bTailer
     * @param <A>
     * @param <B>
     * @param <C>
     * @return
     */
    public static <A, B, C> Stream<C> biMapTail(Stream<? extends A> first, Stream<? extends B> second,
                                                BiFunction<? super A, ? super B, ? extends C> mapper,
                                                Function<? super A, ? extends C> aTailer,
                                                Function<? super B, ? extends C> bTailer) {
        Objects.requireNonNull(mapper);
        Objects.requireNonNull(aTailer);
        Objects.requireNonNull(bTailer);

        Spliterator<? extends A> aSpliterator = Objects.requireNonNull(first).spliterator();
        Spliterator<? extends B> bSpliterator = Objects.requireNonNull(second).spliterator();

        Iterator<A> aIterator = Spliterators.iterator(aSpliterator);
        Iterator<B> bIterator = Spliterators.iterator(bSpliterator);
        Iterator<C> cIterator = new BiMapTailIterator<>(aIterator, bIterator, mapper, aTailer, bTailer);
        boolean parallel = first.isParallel() || second.isParallel();
        int lostCharacteristics = Spliterator.DISTINCT | Spliterator.SORTED;
        BinaryOperator<Long> sizeOperator = Math::max;
        return getStreamFromIterators(aSpliterator, bSpliterator, cIterator, sizeOperator, parallel, lostCharacteristics);

    }

    /**
     * Provides a stream resulting of mapping both streams elements, with the difference that elements 
     * will not be paired with elements on same "position",
     * but the pairing element will be decided by a "comparator" function.
     * The pseudo-code for deciding if two elements should be mapped together is:
     * <pre>
     *     Beginning with head elements from both streams:
     *     if "comparator" function returns '0', `biMapper` function is applied to both elements, and next element is picked from both streams.
     *     if "comparator" function returns <0 value, `mapper` function is applied to the element from the first stream, and next element is picked from first stream
     *     if "comparator" function returns >0 value, `mapper` function is applied to the element from the second stream, and next element is picked from second stream
     * </pre>
     * In other words, the "comparator" function is a `comparator` for stream elements.
     * If both elements are "equals" given that comparator, then `biMapper` is applied to both. Else, `mapper` is applied to the 'minor' element.
     * Note that this behaviour assumes elements on both streams are somehow 'sorted' given the comparator function.
     *
     * @param first
     * @param second
     * @param comparator
     * @param biMapper
     * @param mapper
     * @param <A>
     * @param <C>
     * @return
     */
    public static <A, C> Stream<C> comparingBiMap(Stream<? extends A> first, Stream<? extends A> second,
                                                  BiFunction<? super A, ? super A, Integer> comparator,
                                                  BiFunction<? super A, ? super A, ? extends C> biMapper,
                                                  Function<? super A, ? extends C> mapper) {
        return comparingBiMap(first, second, comparator, biMapper, mapper, mapper);
    }

    /**
     * Same than equally-named method, but this one accepts two different `mappers`, one for each stream.
     * This also allows stream to contain elements of different types, as long as both biMapper and mapper's produce a common type (C).
     *
     * @param first
     * @param second
     * @param comparator
     * @param biMapper
     * @param aMapper
     * @param bMapper
     * @param <A>
     * @param <B>
     * @param <C>
     * @return
     */
    public static <A, B, C> Stream<C> comparingBiMap(Stream<? extends A> first, Stream<? extends B> second,
                                                     BiFunction<? super A, ? super B, Integer> comparator,
                                                     BiFunction<? super A, ? super B, ? extends C> biMapper,
                                                     Function<? super A, ? extends C> aMapper,
                                                     Function<? super B, ? extends C> bMapper) {
        Objects.requireNonNull(biMapper);
        Objects.requireNonNull(aMapper);
        Objects.requireNonNull(bMapper);

        Spliterator<? extends A> aSpliterator = Objects.requireNonNull(first).spliterator();
        Spliterator<? extends B> bSpliterator = Objects.requireNonNull(second).spliterator();

        Iterator<A> aIterator = Spliterators.iterator(aSpliterator);
        Iterator<B> bIterator = Spliterators.iterator(bSpliterator);
        Iterator<C> cIterator = new SteppingBiMapTailIterator<>(aIterator, bIterator, biMapper, comparator, aMapper, bMapper);
        boolean parallel = first.isParallel() || second.isParallel();
        int lostCharacteristics = Spliterator.DISTINCT | Spliterator.SORTED | Spliterator.SIZED;
        BinaryOperator<Long> sizeOperator = null;
        return getStreamFromIterators(aSpliterator, bSpliterator, cIterator, sizeOperator, parallel, lostCharacteristics);
    }

    private static <C, A, B> Stream<C> getStreamFromIterators(Spliterator<? extends A> aSpliterator, Spliterator<? extends B> bSpliterator, Iterator<C> iterator, BinaryOperator<Long> sizeOperator, boolean parallel, int lostCharacteristics) {
        int characteristics = aSpliterator.characteristics() & bSpliterator.characteristics()
                & ~lostCharacteristics;

        long size = ((characteristics & Spliterator.SIZED) != 0)
                ? sizeOperator.apply(aSpliterator.getExactSizeIfKnown(), bSpliterator.getExactSizeIfKnown())
                : -1L;

        Spliterator<C> split = Spliterators.spliterator(iterator, size, characteristics);
        return StreamSupport.stream(split, parallel);
    }


    private static class ZipIterator<C, A extends C, B extends C> implements Iterator<C> {
        private final Iterator<A> aIterator;
        private final Iterator<B> bIterator;
        private final int batchSize;
        private int batchStep;
        private final boolean keepTail;

        public ZipIterator(Iterator<A> aIterator, Iterator<B> bIterator, int batchSize, boolean keepTail) {
            this.aIterator = aIterator;
            this.bIterator = bIterator;
            this.batchSize = batchSize;
            batchStep = -batchSize;
            this.keepTail = keepTail;
        }

        @Override
        public boolean hasNext() {
            return (keepTail && (aIterator.hasNext() || bIterator.hasNext())) ||
                    (!keepTail && (aIterator.hasNext() && batchStep < 0 || bIterator.hasNext() && batchStep >= 0));
        }

        @Override
        public C next() {
            boolean aOrB = (aIterator.hasNext() && batchStep < 0) || (batchStep > 0 && !bIterator.hasNext() && keepTail);
            batchStep = ++batchStep < batchSize ? batchStep : -batchSize;
            return aOrB ? aIterator.next() : bIterator.next();
        }
    }

    private static class BiMapIterator<C, A, B> implements Iterator<C> {
        private final Iterator<A> aIterator;
        private final Iterator<B> bIterator;
        private final BiFunction<? super A, ? super B, ? extends C> mapper;

        public BiMapIterator(Iterator<A> aIterator, Iterator<B> bIterator,
                             BiFunction<? super A, ? super B, ? extends C> mapper) {
            this.aIterator = aIterator;
            this.bIterator = bIterator;
            this.mapper = mapper;
        }

        @Override
        public boolean hasNext() {
            return aIterator.hasNext() && bIterator.hasNext();
        }

        @Override
        public C next() {
            return mapper.apply(aIterator.next(), bIterator.next());
        }
    }

    private static class BiMapTailIterator<C, A, B> implements Iterator<C> {
        private final Iterator<A> aIterator;
        private final Iterator<B> bIterator;
        private final BiFunction<? super A, ? super B, ? extends C> mapper;
        private final Function<? super A, ? extends C> aTailer;
        private final Function<? super B, ? extends C> bTailer;

        public BiMapTailIterator(Iterator<A> aIterator, Iterator<B> bIterator,
                                 BiFunction<? super A, ? super B, ? extends C> mapper, Function<? super A, ? extends C> aTailer,
                                 Function<? super B, ? extends C> bTailer) {
            this.aIterator = aIterator;
            this.bIterator = bIterator;
            this.mapper = mapper;
            this.aTailer = aTailer;
            this.bTailer = bTailer;
        }

        @Override
        public boolean hasNext() {
            return aIterator.hasNext() || bIterator.hasNext();
        }

        @Override
        public C next() {
            if (aIterator.hasNext() && bIterator.hasNext()) {
                return mapper.apply(aIterator.next(), bIterator.next());
            } else {
                if (aIterator.hasNext()) {
                    return aTailer.apply(aIterator.next());
                } else {
                    return bTailer.apply(bIterator.next());
                }
            }
        }
    }

    private static class SteppingBiMapTailIterator<C, A, B> implements Iterator<C> {
        private final Iterator<A> aIterator;
        private final Iterator<B> bIterator;
        private final BiFunction<? super A, ? super B, ? extends C> mapper;
        private final BiFunction<? super A, ? super B, Integer> comparator;
        private final Function<? super A, ? extends C> aTailer;
        private final Function<? super B, ? extends C> bTailer;
        private A currentA;
        private B currentB;
        private boolean haveA = false;
        private boolean haveB = false;

        public SteppingBiMapTailIterator(Iterator<A> aIterator, Iterator<B> bIterator,
                                         BiFunction<? super A, ? super B, ? extends C> mapper,
                                         BiFunction<? super A, ? super B, Integer> comparator,
                                         Function<? super A, ? extends C> aTailer,
                                         Function<? super B, ? extends C> bTailer) {
            this.aIterator = aIterator;
            this.bIterator = bIterator;
            this.mapper = mapper;
            this.comparator = comparator;
            this.aTailer = aTailer;
            this.bTailer = bTailer;
            init();
        }

        @Override
        public boolean hasNext() {
            return (availableA() || availableB());
        }

        private boolean availableB() {
            return (haveA && haveB && comparator.apply(currentA, currentB) >= 0) || (!haveA && haveB);
        }

        private boolean availableA() {
            return (haveA && haveB && comparator.apply(currentA, currentB) <= 0) || (haveA && !haveB);
        }

        @Override
        public C next() {
            // 1.- Fetch next values, **based on biComparator**
            C result;
            if (availableA() && availableB()) {
                result = mapper.apply(currentA, currentB);
                //TODO: If we want a way to skip elements based on explain (i.e. if explain is null),
                // or even better, pre-filter elements to be mapped (using areEquals),
                // we need to calculate availability in advance, so 'hasNext' can be calculated.
                haveA = stepA();
                haveB = stepB();
            } else if (availableA()) {
                result = aTailer.apply(currentA);
                haveA = stepA();
            } else if (availableB()) {
                result = bTailer.apply(currentB);
                haveB = stepB();
            } else {
                //Should never happen (protected by hasNext);
                result = null;
            }
            return result;

        }

        private void init() {
            haveA = stepA();
            haveB = stepB();
        }

        private boolean stepA() {
            if (aIterator.hasNext()) {
                currentA = aIterator.next();
                return true;
            } else {
                return false;
            }
        }

        private boolean stepB() {
            if (bIterator.hasNext()) {
                currentB = bIterator.next();
                return true;
            } else {
                return false;
            }
        }
    }
}

