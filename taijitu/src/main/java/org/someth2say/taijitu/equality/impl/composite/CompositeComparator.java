package org.someth2say.taijitu.equality.impl.composite;

import org.someth2say.taijitu.equality.aspects.external.Comparator;
import org.someth2say.taijitu.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.equality.impl.delegating.DelegatingComparator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This is the simplest class of CompositeEqualizer. It assumes that:
 * <OL><LI>Both sides of equality are the same type and</LI>
 * <li>Both sides of equality are using the same extractors</li></OL>
 *
 * @param <T>
 */
public class CompositeComparator<T> extends CompositeEqualizer<T> implements ICompositeComparator<T>{


    private final List<? extends Comparator<T>> comparators;

    public CompositeComparator(List<? extends Comparator<T>> comparators){
        this(comparators,comparators);
    }

    private CompositeComparator(List<? extends Comparator<T>> comparators, List<? extends Equalizer<T>> equalizers) {
        super(equalizers);
        this.comparators = comparators;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()
                + comparators.stream().map(Equalizer::toString).collect(Collectors.joining(",","(",")"));
    }

    public List<? extends Comparator<T>> getComparators() {
        return comparators;
    }

    public static class Builder<T> extends CompositeEqualizer.Builder<T> {
        private final List<Comparator<T>> comparators = new ArrayList<>();

        public Builder<T> addComparator(Comparator<T> comparator) {
            super.addEqualizer(comparator);
            comparators.add(comparator);
            return this;
        }

        public <R> Builder<T> addComparator(Function<T,R> extractor, Comparator<R> delegate) {
            Comparator<T> delegatingComparator = new DelegatingComparator<>(extractor, delegate);
            return addComparator(delegatingComparator);
        }

        public CompositeComparator<T> build() {
            return new CompositeComparator<>(getComparators(), getEqualizers());
        }

        public List<? extends Comparator<T>> getComparators() {
            return comparators;
        }
    }

}

interface ICompositeComparator<T> extends Comparator<T> {

    @Override
    default int compare(T first, T second) {
        return this.getComparators().stream()
                .map(comparator -> comparator.compare(first, second))
                .filter(i -> i != 0)
                .findFirst()
                .orElse(0);
    }

    List<? extends Comparator<T>> getComparators();
}