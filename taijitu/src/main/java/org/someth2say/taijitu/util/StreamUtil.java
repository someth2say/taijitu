package org.someth2say.taijitu.util;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * From
 * https://stackoverflow.com/questions/17640754/zipping-streams-using-jdk8-with-lambda-java-util-stream-streams-zip
 */
public class StreamUtil {

    public static <C, A extends C, B extends C> Stream<C> zip(Stream<? extends A> a, Stream<? extends B> b) {
        return zip(a, b, 1);
    }

    public static <C, A extends C, B extends C> Stream<C> zip(Stream<? extends A> a, Stream<? extends B> b, int batchSize) {
        Spliterator<? extends A> aSpliterator = Objects.requireNonNull(a).spliterator();
        Spliterator<? extends B> bSpliterator = Objects.requireNonNull(b).spliterator();

        Iterator<A> aIterator = Spliterators.iterator(aSpliterator);
        Iterator<B> bIterator = Spliterators.iterator(bSpliterator);

        Iterator<C> zipIterator = new ZipIterator<>(aIterator, bIterator, batchSize);
        return getStreamFromIterator(a, b, aSpliterator, bSpliterator, zipIterator, Math::addExact);

    }

    public static <A, B, C> Stream<C> biMap(Stream<? extends A> a, Stream<? extends B> b,
                                            BiFunction<? super A, ? super B, ? extends C> biFunction) {
        Objects.requireNonNull(biFunction);
        Spliterator<? extends A> aSpliterator = Objects.requireNonNull(a).spliterator();
        Spliterator<? extends B> bSpliterator = Objects.requireNonNull(b).spliterator();

        Iterator<A> aIterator = Spliterators.iterator(aSpliterator);
        Iterator<B> bIterator = Spliterators.iterator(bSpliterator);
        Iterator<C> cIterator = new BiMapIterator<>(aIterator, bIterator, biFunction);
        return getStreamFromIterator(a, b, aSpliterator, bSpliterator, cIterator, Math::min);

    }

    public static <A, C> Stream<C> biMapTail(Stream<? extends A> a, Stream<? extends A> b,
                                             BiFunction<? super A, ? super A, ? extends C> biFunction, Function<? super A, ? extends C> tailer) {
        return biMapTail(a, b, biFunction, tailer, tailer);
    }

    public static <A, B, C> Stream<C> biMapTail(Stream<? extends A> a, Stream<? extends B> b,
                                                BiFunction<? super A, ? super B, ? extends C> biFunction, Function<? super A, ? extends C> aTailer,
                                                Function<? super B, ? extends C> bTailer) {
        Objects.requireNonNull(biFunction);
        Objects.requireNonNull(aTailer);
        Objects.requireNonNull(bTailer);

        Spliterator<? extends A> aSpliterator = Objects.requireNonNull(a).spliterator();
        Spliterator<? extends B> bSpliterator = Objects.requireNonNull(b).spliterator();

        Iterator<A> aIterator = Spliterators.iterator(aSpliterator);
        Iterator<B> bIterator = Spliterators.iterator(bSpliterator);
        Iterator<C> cIterator = new BiMapTailIterator<>(aIterator, bIterator, biFunction, aTailer, bTailer);
        return getStreamFromIterator(a, b, aSpliterator, bSpliterator, cIterator, Math::max);
    }

    public static <A, B, C> Stream<C> steppingBiMapTail(Stream<? extends A> a, Stream<? extends B> b,
                                                        BiFunction<? super A, ? super B, ? extends C> biFunction, BiFunction<? super A, ? super B, Integer> biComparator, Function<? super A, ? extends C> aTailer,
                                                        Function<? super B, ? extends C> bTailer) {
        Objects.requireNonNull(biFunction);
        Objects.requireNonNull(aTailer);
        Objects.requireNonNull(bTailer);

        Spliterator<? extends A> aSpliterator = Objects.requireNonNull(a).spliterator();
        Spliterator<? extends B> bSpliterator = Objects.requireNonNull(b).spliterator();

        Iterator<A> aIterator = Spliterators.iterator(aSpliterator);
        Iterator<B> bIterator = Spliterators.iterator(bSpliterator);
        Iterator<C> cIterator = new SteppingBiMapTailIterator<>(aIterator, bIterator, biFunction, biComparator, aTailer, bTailer);
        return getStreamFromIterator(a, b, aSpliterator, bSpliterator, cIterator, Math::max);
    }

    private static <C, A, B> Stream<C> getStreamFromIterator(Stream<? extends A> a, Stream<? extends B> b,
                                                             Spliterator<? extends A> aSpliterator, Spliterator<? extends B> bSpliterator, Iterator<C> zipIterator,
                                                             BinaryOperator<Long> sizeOperator) {
        // Zipping looses DISTINCT and SORTED characteristics
        int characteristics = aSpliterator.characteristics() & bSpliterator.characteristics()
                & ~(Spliterator.DISTINCT | Spliterator.SORTED);

        long zipSize = ((characteristics & Spliterator.SIZED) != 0)
                ? sizeOperator.apply(aSpliterator.getExactSizeIfKnown(), bSpliterator.getExactSizeIfKnown())
                : -1;

        Spliterator<C> split = Spliterators.spliterator(zipIterator, zipSize, characteristics);
        return StreamSupport.stream(split, a.isParallel() || b.isParallel());
    }


    public static class ZipIterator<C, A extends C, B extends C> implements Iterator<C> {
        private final Iterator<A> aIterator;
        private final Iterator<B> bIterator;
        private final int batchSize;
        int batchStep;

        public ZipIterator(Iterator<A> aIterator, Iterator<B> bIterator, int batchSize) {
            this.aIterator = aIterator;
            this.bIterator = bIterator;
            this.batchSize = batchSize;
            batchStep = -batchSize;
        }

        @Override
        public boolean hasNext() {
            return aIterator.hasNext() || bIterator.hasNext();
        }

        @Override
        public C next() {
            boolean aOrB = aIterator.hasNext() && batchStep < 0;
            batchStep = ++batchStep < batchSize ? batchStep : -batchSize;
            return aOrB ? aIterator.next() : bIterator.next();
        }
    }

    public static class BiMapIterator<C, A, B> implements Iterator<C> {
        private final Iterator<A> aIterator;
        private final Iterator<B> bIterator;
        private final BiFunction<? super A, ? super B, ? extends C> biFunction;

        public BiMapIterator(Iterator<A> aIterator, Iterator<B> bIterator,
                             BiFunction<? super A, ? super B, ? extends C> biFunction) {
            this.aIterator = aIterator;
            this.bIterator = bIterator;
            this.biFunction = biFunction;
        }

        @Override
        public boolean hasNext() {
            return aIterator.hasNext() && bIterator.hasNext();
        }

        @Override
        public C next() {
            return biFunction.apply(aIterator.next(), bIterator.next());
        }
    }

    public static class BiMapTailIterator<C, A, B> implements Iterator<C> {
        private final Iterator<A> aIterator;
        private final Iterator<B> bIterator;
        private final BiFunction<? super A, ? super B, ? extends C> biFunction;
        private final Function<? super A, ? extends C> aTailer;
        private final Function<? super B, ? extends C> bTailer;

        public BiMapTailIterator(Iterator<A> aIterator, Iterator<B> bIterator,
                                 BiFunction<? super A, ? super B, ? extends C> biFunction, Function<? super A, ? extends C> aTailer,
                                 Function<? super B, ? extends C> bTailer) {
            this.aIterator = aIterator;
            this.bIterator = bIterator;
            this.biFunction = biFunction;
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
                return biFunction.apply(aIterator.next(), bIterator.next());
            } else {
                if (aIterator.hasNext()) {
                    return aTailer.apply(aIterator.next());
                } else {
                    return bTailer.apply(bIterator.next());
                }
            }
        }
    }

    public static class SteppingBiMapTailIterator<C, A, B> implements Iterator<C> {
        private final Iterator<A> aIterator;
        private final Iterator<B> bIterator;
        private final BiFunction<? super A, ? super B, ? extends C> biFunction;
        private final BiFunction<? super A, ? super B, Integer> biComparator;
        private final Function<? super A, ? extends C> aTailer;
        private final Function<? super B, ? extends C> bTailer;
        private A currentA;
        private B currentB;
        private boolean haveA = false;
        private boolean haveB = false;

        public SteppingBiMapTailIterator(Iterator<A> aIterator, Iterator<B> bIterator,
                                         BiFunction<? super A, ? super B, ? extends C> biFunction,
                                         BiFunction<? super A, ? super B, Integer> biComparator,
                                         Function<? super A, ? extends C> aTailer,
                                         Function<? super B, ? extends C> bTailer) {
            this.aIterator = aIterator;
            this.bIterator = bIterator;
            this.biFunction = biFunction;
            this.biComparator = biComparator;
            this.aTailer = aTailer;
            this.bTailer = bTailer;
            init();
        }

        @Override
        public boolean hasNext() {
            return (shouldUseA() || shouldUseB());
        }

        public boolean shouldUseB() {
            return (haveA && haveB &&biComparator.apply(currentA, currentB) >= 0) || (!haveA && haveB);
        }

        public boolean shouldUseA() {
            return (haveA && haveB && biComparator.apply(currentA, currentB) <= 0) || (haveA && !haveB);
        }

        @Override
        public C next() {
            // 1.- Fetch next values, **based on biComparator**

            C result;
            if (shouldUseA() && shouldUseB()) {
                result = biFunction.apply(currentA, currentB);
                haveA = stepA();
                haveB = stepB();
            } else if (shouldUseA()) {
                result = aTailer.apply(currentA);
                haveA = stepA();
            } else if (shouldUseB()) {
                result = bTailer.apply(currentB);
                haveB = stepB();
            } else {
                //Should never happen (protected by hasNext);
                result = null;
            }
            return result;

        }

        public void init() {
            haveA = stepA();
            haveB = stepB();
        }

        public boolean stepA() {
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

