package org.someth2say.taijitu.util;

import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * From https://stackoverflow.com/questions/17640754/zipping-streams-using-jdk8-with-lambda-java-util-stream-streams-zip
 */
public class StreamUtil {

    public static <C, A extends C, B extends C> Stream<C> interleave(Stream<? extends A> a, Stream<? extends B> b) {
        Spliterator<? extends A> aSpliterator = Objects.requireNonNull(a).spliterator();
        Spliterator<? extends B> bSpliterator = Objects.requireNonNull(b).spliterator();

        Iterator<A> aIterator = Spliterators.iterator(aSpliterator);
        Iterator<B> bIterator = Spliterators.iterator(bSpliterator);

        Iterator<C> zipIterator = new ZipIterator<>(aIterator, bIterator);
        return getStreamFromIterator(a, b, aSpliterator, bSpliterator, zipIterator, Math::addExact);

    }

    private static <C, A, B> Stream<C> getStreamFromIterator(Stream<? extends A> a, Stream<? extends B> b, Spliterator<? extends A> aSpliterator, Spliterator<? extends B> bSpliterator, Iterator<C> zipIterator, BinaryOperator<Long> sizeOperator) {
        // Zipping looses DISTINCT and SORTED characteristics
        int characteristics = aSpliterator.characteristics() & bSpliterator.characteristics() &
                ~(Spliterator.DISTINCT | Spliterator.SORTED);

        long zipSize = ((characteristics & Spliterator.SIZED) != 0)
                ? sizeOperator.apply(aSpliterator.getExactSizeIfKnown(), bSpliterator.getExactSizeIfKnown())
                : -1;

        Spliterator<C> split = Spliterators.spliterator(zipIterator, zipSize, characteristics);
        return StreamSupport.stream(split, a.isParallel() || b.isParallel());
    }


    public static <A, B, C> Stream<C> zipMap(Stream<? extends A> a, Stream<? extends B> b,
                                             BiFunction<? super A, ? super B, ? extends C> zipper) {
        Objects.requireNonNull(zipper);
        Spliterator<? extends A> aSpliterator = Objects.requireNonNull(a).spliterator();
        Spliterator<? extends B> bSpliterator = Objects.requireNonNull(b).spliterator();

        Iterator<A> aIterator = Spliterators.iterator(aSpliterator);
        Iterator<B> bIterator = Spliterators.iterator(bSpliterator);
        Iterator<C> cIterator = new ZipMapIterator<>(aIterator, bIterator, zipper);
        return getStreamFromIterator(a, b, aSpliterator, bSpliterator, cIterator, Math::min);

    }

    public static <A, C> Stream<C> zipMapTail(Stream<? extends A> a, Stream<? extends A> b,
                                                 BiFunction<? super A, ? super A, ? extends C> zipper,
                                                 Function<? super A, ? extends C> tailer,
                                                 ) {

    }


    public static <A, B, C> Stream<C> zipMapTail(Stream<? extends A> a, Stream<? extends B> b,
                                                 BiFunction<? super A, ? super B, ? extends C> zipper,
                                                 Function<? super A, ? extends C> aTailer,
                                                 Function<? super B, ? extends C> bTailer) {
        Objects.requireNonNull(zipper);
        Objects.requireNonNull(aTailer);
        Objects.requireNonNull(bTailer);

        Spliterator<? extends A> aSpliterator = Objects.requireNonNull(a).spliterator();
        Spliterator<? extends B> bSpliterator = Objects.requireNonNull(b).spliterator();

        Iterator<A> aIterator = Spliterators.iterator(aSpliterator);
        Iterator<B> bIterator = Spliterators.iterator(bSpliterator);
        Iterator<C> cIterator = new ZipMapTailIterator<>(aIterator, bIterator, zipper, aTailer, bTailer);
        return getStreamFromIterator(a, b, aSpliterator, bSpliterator, cIterator, Math::max);

    }


    public static class ZipIterator<C, A extends C, B extends C> implements Iterator<C> {
        private final Iterator<A> aIterator;
        private final Iterator<B> bIterator;
        boolean flag;

        public ZipIterator(Iterator<A> aIterator, Iterator<B> bIterator) {
            this.aIterator = aIterator;
            this.bIterator = bIterator;
            flag = false;
        }

        @Override
        public boolean hasNext() {
            return aIterator.hasNext() || bIterator.hasNext();
        }

        @Override
        public C next() {
            flag = !flag;
            boolean aOrB = aIterator.hasNext() && bIterator.hasNext() ? flag : aIterator.hasNext();
            return aOrB ? aIterator.next() : bIterator.next();
        }
    }

    public static class ZipMapIterator<C, A, B> implements Iterator<C> {
        private final Iterator<A> aIterator;
        private final Iterator<B> bIterator;
        private final BiFunction<? super A, ? super B, ? extends C> zipper;

        public ZipMapIterator(Iterator<A> aIterator, Iterator<B> bIterator, BiFunction<? super A, ? super B, ? extends C> zipper) {
            this.aIterator = aIterator;
            this.bIterator = bIterator;
            this.zipper = zipper;
        }

        @Override
        public boolean hasNext() {
            return aIterator.hasNext() && bIterator.hasNext();
        }

        @Override
        public C next() {
            return zipper.apply(aIterator.next(), bIterator.next());
        }
    }

    public static class ZipMapTailIterator<C, A, B> implements Iterator<C> {
        private final Iterator<A> aIterator;
        private final Iterator<B> bIterator;
        private final BiFunction<? super A, ? super B, ? extends C> zipper;
        private final Function<? super A, ? extends C> aTailer;
        private final Function<? super B, ? extends C> bTailer;

        public ZipMapTailIterator(Iterator<A> aIterator, Iterator<B> bIterator, BiFunction<? super A, ? super B, ? extends C> zipper, Function<? super A, ? extends C> aTailer, Function<? super B, ? extends C> bTailer) {
            this.aIterator = aIterator;
            this.bIterator = bIterator;
            this.zipper = zipper;
            this.aTailer = aTailer;
            this.bTailer = bTailer;
        }

        @Override
        public boolean hasNext() {
            return aIterator.hasNext() || bIterator.hasNext();
        }

        @Override
        public C next() {
            return aIterator.hasNext() && bIterator.hasNext()
                    ? zipper.apply(aIterator.next(), bIterator.next())
                    : aIterator.hasNext()
                    ? aTailer.apply(aIterator.next())
                    : bTailer.apply(bIterator.next());
        }
    }
}
