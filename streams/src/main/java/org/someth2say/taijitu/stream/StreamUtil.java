package org.someth2say.taijitu.stream;

import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.*;
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
     * <pre>
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

        Objects.requireNonNull(first);
        Objects.requireNonNull(second);

        Spliterator<? extends A> aSpliterator = first.spliterator();
        Spliterator<? extends B> bSpliterator = second.spliterator();

        Iterator<A> aIterator = Spliterators.iterator(aSpliterator);
        Iterator<B> bIterator = Spliterators.iterator(bSpliterator);

        Iterator<C> zipIterator = new ZipIterator<>(aIterator, bIterator, batchSize, keepTail);

        boolean parallel = first.isParallel() || second.isParallel();
        int lostCharacteristics = Spliterator.DISTINCT | Spliterator.SORTED | Spliterator.SIZED;
        BinaryOperator<Long> sizeOperator = null;
        // SIZE characteristic is only retained both streams are sized,
        if (aSpliterator.hasCharacteristics(Spliterator.SIZED) && bSpliterator.hasCharacteristics(Spliterator.SIZED)) { // T
            // Result stream size will be the common batchSize for both streams, and up to one more if available in first stream.
            lostCharacteristics = Spliterator.DISTINCT | Spliterator.SORTED;
            sizeOperator = (aSize, bSize) -> Math.min(aSize % batchSize, bSize % batchSize) + aSize > bSize ? Math.min(aSize - bSize, batchSize) : 0;
        }

        return getStreamFromIterators(aSpliterator, bSpliterator, zipIterator, sizeOperator, parallel, lostCharacteristics);
    }


    public static <A, B, C> Stream<C> biMap(Stream<? extends A> first,
                                            Stream<? extends B> second,
                                            BiStreamFunctions<A, B, C> funcs) {
        TriFunction<Iterator<A>, Iterator<B>, BiStreamFunctions<A, B, C>, Iterator<C>> iteratorBuilder = BiMapIterator::new;
        int lostCharacteristics = Spliterator.DISTINCT | Spliterator.SORTED;
        BinaryOperator<Long> sizeOperator = Math::min;

        return getcStream(first, second, funcs, iteratorBuilder, lostCharacteristics, sizeOperator);
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
     * @param <A>
     * @param <B>
     * @param <C>
     * @param first
     * @param second
     * @param funcs
     * @return
     */
    public static <A, B, C> Stream<C> comparingBiMap(Stream<? extends A> first,
                                                     Stream<? extends B> second,
                                                     BiStreamFunctions<A, B, C> funcs) {
        TriFunction<Iterator<A>, Iterator<B>, BiStreamFunctions<A, B, C>, Iterator<C>> iteratorBuilder = ComparingBiMapIterator::new;
        int lostCharacteristics = Spliterator.DISTINCT | Spliterator.SORTED | Spliterator.SIZED;
        BinaryOperator<Long> sizeOperator = null;

        return getcStream(first, second, funcs, iteratorBuilder, lostCharacteristics, sizeOperator);
    }

    private static <A, B, C> Stream<C> getcStream(Stream<? extends A> first,
                                                  Stream<? extends B> second,
                                                  BiStreamFunctions<A, B, C> funcs,
                                                  TriFunction<Iterator<A>, Iterator<B>, BiStreamFunctions<A, B, C>, Iterator<C>> iteratorBuilder,
                                                  int lostCharacteristics,
                                                  BinaryOperator<Long> sizeOperator) {
        Objects.requireNonNull(first);
        Objects.requireNonNull(second);
        Objects.requireNonNull(funcs);

        Spliterator<? extends A> aSpliterator = first.spliterator();
        Spliterator<? extends B> bSpliterator = second.spliterator();

        Iterator<A> aIterator = Spliterators.iterator(aSpliterator);
        Iterator<B> bIterator = Spliterators.iterator(bSpliterator);

        Iterator<C> cIterator = iteratorBuilder.apply(aIterator, bIterator, funcs);

        boolean parallel = first.isParallel() || second.isParallel();
        return getStreamFromIterators(aSpliterator, bSpliterator, cIterator, sizeOperator, parallel, lostCharacteristics);
    }

    private static <C, A, B> Stream<C> getStreamFromIterators(Spliterator<? extends A> aSpliterator,
                                                              Spliterator<? extends B> bSpliterator,
                                                              Iterator<C> iterator,
                                                              BinaryOperator<Long> sizeOperator,
                                                              boolean parallel,
                                                              int lostCharacteristics) {
        int characteristics = aSpliterator.characteristics() & bSpliterator.characteristics()
                & ~lostCharacteristics;

        long size = ((characteristics & Spliterator.SIZED) != 0)
                ? sizeOperator.apply(aSpliterator.getExactSizeIfKnown(), bSpliterator.getExactSizeIfKnown())
                : -1L;

        Spliterator<C> split = Spliterators.spliterator(iterator, size, characteristics);
        return StreamSupport.stream(split, parallel);
    }

    ///////// ITERATORS /////////

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
            boolean aOrB = (aIterator.hasNext() && batchStep < 0) || (batchStep >= 0 && !bIterator.hasNext() && keepTail);
            batchStep = ++batchStep < batchSize ? batchStep : -batchSize;
            return aOrB ? aIterator.next() : bIterator.next();
        }
    }

    private static class PreFetchIterator<T> implements Iterator<T> {
        private final Iterator<T> aIterator;
        private T preFetched;
        private boolean hasNext = false;

        public PreFetchIterator(Iterator<T> aIterator) {
            Objects.requireNonNull(aIterator);
            this.aIterator = aIterator;
            preFetch();
        }

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public T next() {
            T next = this.preFetched;
            preFetch();
            return next;
        }

        public T peek() {
            return this.preFetched;
        }

        private void preFetch() {
            if (aIterator.hasNext()) {
                this.preFetched = aIterator.next();
                hasNext = true;
            } else {
                this.preFetched = null;
                hasNext = false;
            }
        }
    }

    private static abstract class PreFetchBiIterator<A, B, C> implements Iterator<C> {

        public final PreFetchIterator<A> pIteratorA;
        public final PreFetchIterator<B> pIteratorB;
        public final BiStreamFunctions<A, B, C> funcs;

        public PreFetchBiIterator(Iterator<A> aIterator, Iterator<B> bIterator, BiStreamFunctions<A, B, C> funcs) {
            Objects.requireNonNull(funcs);
            Objects.requireNonNull(funcs.biFilter);
            this.pIteratorA = new PreFetchIterator<>(aIterator);
            this.pIteratorB = new PreFetchIterator<>(bIterator);
            this.funcs = funcs;
        }

        @Override
        public boolean hasNext() {
            while (pIteratorA.hasNext() && pIteratorB.hasNext()) {
                if (!funcs.biFilter.test(pIteratorA.peek(), pIteratorB.peek())) {
                    return true;
                } else {
                    pIteratorA.next();
                    pIteratorB.next();
                }
            }
            return funcs.keepTail && ((pIteratorA.hasNext()) || (pIteratorB.hasNext()));
        }
    }

    private static class BiMapIterator<A, B, C> extends PreFetchBiIterator<A, B, C> {

        public BiMapIterator(Iterator<A> aIterator, Iterator<B> bIterator,
                                 BiStreamFunctions<A, B, C> funcs) {

            super(aIterator, bIterator, funcs);
            Objects.requireNonNull(funcs.biMapper);
            if (funcs.keepTail) {
                Objects.requireNonNull(funcs.aMapper);
                Objects.requireNonNull(funcs.bMapper);
            }
        }

        @Override
        public C next() {
            if (pIteratorA.hasNext() && pIteratorB.hasNext()) {
                return funcs.biMapper.apply(pIteratorA.next(), pIteratorB.next());
            } else if (funcs.keepTail && pIteratorA.hasNext()) {
                return funcs.aMapper.apply(pIteratorA.next());
            } else if (funcs.keepTail && pIteratorB.hasNext()) {
                return funcs.bMapper.apply(pIteratorB.next());
            } else {
                //Should never happen (protected by hasNext);
                return null;
            }
        }
    }

    private static class ComparingBiMapIterator<A, B, C> extends PreFetchBiIterator<A, B, C> {

        public ComparingBiMapIterator(Iterator<A> aIterator,
                                      Iterator<B> bIterator,
                                      BiStreamFunctions<A, B, C> funcs) {

            super(aIterator, bIterator, funcs);
            Objects.requireNonNull(funcs.comparator);
            Objects.requireNonNull(funcs.biMapper);
            Objects.requireNonNull(funcs.aMapper);
            Objects.requireNonNull(funcs.bMapper);
        }

        @Override
        public C next() {
            if (pIteratorA.hasNext() && pIteratorB.hasNext() && funcs.comparator.apply(pIteratorA.peek(), pIteratorB.peek()) == 0) {
                return funcs.biMapper.apply(pIteratorA.next(), pIteratorB.next());
            } else if ((pIteratorA.hasNext() && pIteratorB.hasNext() && funcs.comparator.apply(pIteratorA.peek(), pIteratorB.peek()) < 0) || (pIteratorA.hasNext() && !pIteratorB.hasNext())) {
                return funcs.aMapper.apply(pIteratorA.next());
            } else if ((pIteratorA.hasNext() && pIteratorB.hasNext() && funcs.comparator.apply(pIteratorA.peek(), pIteratorB.peek()) > 0) || (!pIteratorA.hasNext() && pIteratorB.hasNext())) {
                return funcs.bMapper.apply(pIteratorB.next());
            } else {
                //Should never happen (protected by hasNext);
                return null;
            }
        }
    }

    /////// UTILITY CLASSES //////////
    public static class BiStreamFunctions<A, B, C> {
        BiFunction<? super A, ? super B, Integer> comparator;
        BiFunction<? super A, ? super B, ? extends C> biMapper;
        Function<? super A, ? extends C> aMapper;
        Function<? super B, ? extends C> bMapper;
        BiPredicate<? super A, ? super B> biFilter;
        boolean keepTail;

        public BiStreamFunctions(BiFunction<? super A, ? super B, Integer> comparator, BiFunction<? super A, ? super B, ? extends C> biMapper, Function<? super A, ? extends C> aMapper, Function<? super B, ? extends C> bMapper, BiPredicate<? super A, ? super B> biFilter, boolean keepTail) {
            this.comparator = comparator;
            this.biMapper = biMapper;
            this.aMapper = aMapper;
            this.bMapper = bMapper;
            this.biFilter = biFilter;
            this.keepTail = keepTail;
        }
    }

    public static class BiStreamFunctionsBuilder<A, B, C> {
        private BiFunction<? super A, ? super B, Integer> comparator;
        private BiFunction<? super A, ? super B, ? extends C> biMapper;
        private Function<? super A, ? extends C> aMapper;
        private Function<? super B, ? extends C> bMapper;
        private BiPredicate<? super A, ? super B> biFilter = (x, y) -> true;
        boolean keepTail = false;

        public BiStreamFunctionsBuilder<A, B, C> setComparator(BiFunction<? super A, ? super B, Integer> comparator) {
            this.comparator = comparator;
            return this;
        }

        public BiStreamFunctionsBuilder<A, B, C> setBiMapper(BiFunction<? super A, ? super B, ? extends C> biMapper) {
            this.biMapper = biMapper;
            return this;
        }

        public BiStreamFunctionsBuilder<A, B, C> setaMapper(Function<? super A, ? extends C> aMapper) {
            this.aMapper = aMapper;
            return this;
        }

        public BiStreamFunctionsBuilder<A, B, C> setbMapper(Function<? super B, ? extends C> bMapper) {
            this.bMapper = bMapper;
            return this;
        }

        public BiStreamFunctionsBuilder<A, B, C> setBiFilter(BiPredicate<? super A, ? super B> biFilter) {
            this.biFilter = biFilter;
            return this;
        }

        public BiStreamFunctionsBuilder<A, B, C> setKeepTail(boolean keepTail) {
            this.keepTail = keepTail;
            return this;
        }

        public BiStreamFunctions<A, B, C> build() {
            return new BiStreamFunctions<>(comparator, biMapper, aMapper, bMapper, biFilter, keepTail);
        }
    }

    @FunctionalInterface
    private interface TriFunction<A, B, C, D> {
        D apply(A a, B b, C c);
    }

    ///////// EASY ENTRYPOINTS ///////

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
    public static <A, B, C> Stream<C> biMap(Stream<? extends A> first,
                                            Stream<? extends B> second,
                                            BiFunction<? super A, ? super B, ? extends C> mapper,
                                            BiPredicate<? super A, ? super B> biFilter) {
        BiStreamFunctions<A, B, C> funcs = new BiStreamFunctionsBuilder<A, B, C>()
                .setBiMapper(mapper)
                .setBiFilter(biFilter)
                .setKeepTail(false)
                .build();
        return biMap(first, second, funcs);
    }

    public static <A, C> Stream<C> biMapTail(Stream<? extends A> first,
                                             Stream<? extends A> second,
                                             BiFunction<? super A, ? super A, C> biMapper,
                                             Function<? super A, ? extends C> tailMapper,
                                             BiPredicate<? super A, ? super A> biFilter) {
        BiStreamFunctions<A, A, C> funcs = new BiStreamFunctionsBuilder<A, A, C>()
                .setBiMapper(biMapper)
                .setaMapper(tailMapper).setbMapper(tailMapper)
                .setBiFilter(biFilter)
                .setKeepTail(true)
                .build();
        return biMap(first, second, funcs);
    }


    public static <A, C> Stream<C> comparingBiMapTail(Stream<? extends A> first,
                                                      Stream<? extends A> second,
                                                      BiFunction<? super A, ? super A, Integer> comparator,
                                                      BiFunction<? super A, ? super A, ? extends C> biMapper,
                                                      Function<? super A, ? extends C> tailMapper,
                                                      BiPredicate<? super A, ? super A> biFilter) {
        BiStreamFunctions<A, A, C> funcs = new BiStreamFunctionsBuilder<A, A, C>()
                .setComparator(comparator)
                .setBiMapper(biMapper)
                .setaMapper(tailMapper).setbMapper(tailMapper)
                .setBiFilter(biFilter)
                .setKeepTail(true)
                .build();
        return comparingBiMap(first, second, funcs);
    }

    public static <A, C> Stream<C> comparingBiMap(Stream<? extends A> first,
                                                  Stream<? extends A> second,
                                                  BiFunction<? super A, ? super A, Integer> comparator,
                                                  BiFunction<? super A, ? super A, ? extends C> biMapper,
                                                  Function<? super A, ? extends C> tailMapper,
                                                  BiPredicate<? super A, ? super A> biFilter) {

        BiStreamFunctions<A, A, C> funcs = new BiStreamFunctionsBuilder<A, A, C>()
                .setComparator(comparator)
                .setBiMapper(biMapper)
                .setBiFilter(biFilter)
                .setaMapper(tailMapper).setbMapper(tailMapper)
                .setKeepTail(false)
                .build();
        return comparingBiMap(first, second, funcs);

    }
}

