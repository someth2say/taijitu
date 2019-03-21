package org.someth2say.taijitu.equality.impl.composite;

import org.someth2say.taijitu.equality.aspects.external.Comparator;
import org.someth2say.taijitu.equality.aspects.external.ComparatorHasher;
import org.someth2say.taijitu.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.equality.aspects.external.Hasher;
import org.someth2say.taijitu.equality.impl.delegating.DelegatingComparatorHasher;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CompositeComparatorHasher<T> extends CompositeEqualizer<T> implements ICompositeComparatorHasher<T> {

    private final List<? extends Comparator<T>> comparators;
    private final List<? extends Hasher<T>> hashers;

    public CompositeComparatorHasher(List<? extends ComparatorHasher<T>> comparatorHashers) {
        super(comparatorHashers);
        this.comparators = comparatorHashers;
        this.hashers = comparatorHashers;
    }

    private CompositeComparatorHasher(List<? extends Comparator<T>> comparators, List<? extends Hasher<T>> hashers,List<? extends Equalizer<T>> equalizers) {
        super(equalizers);
        this.comparators = comparators;
        this.hashers = hashers;
    }

    @Override
    public List<? extends Comparator<T>> getComparators() {
        return comparators;
    }

    @Override
    public List<? extends Hasher<T>> getHashers() {
        return hashers;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()
                + comparators.stream().map(Equalizer::toString).collect(Collectors.joining(",","(",")"))
                + hashers.stream().map(Equalizer::toString).collect(Collectors.joining(",","(",")"));
    }

    public static class Builder<T> extends CompositeEqualizer.Builder<T> {


        private final List<Comparator<T>> comparators = new ArrayList<>();
        private final List<Hasher<T>> hashers = new ArrayList<>();

        public Builder<T> addComparatorHasher(ComparatorHasher<T> comparatorHasher) {
            super.addEqualizer(comparatorHasher);
            comparators.add(comparatorHasher);
            hashers.add(comparatorHasher);
            return this;
        }

        public Builder<T> addHasher(Hasher<T> hasher) {
            addEqualizer(hasher);
            hashers.add(hasher);
            return this;
        }

        public Builder<T> addComparator(Comparator<T> comparator) {
            super.addEqualizer(comparator);
            comparators.add(comparator);
            return this;
        }

        public <R> Builder<T> addComparatorHasher(Function<T, R> extractor, ComparatorHasher<R> delegate) {
            ComparatorHasher<T> delegatingComparatorHasher = new DelegatingComparatorHasher<>(extractor, delegate);
            return addComparatorHasher(delegatingComparatorHasher);
        }


        public CompositeComparatorHasher<T> build() {
            return new CompositeComparatorHasher<T>(comparators, hashers, getEqualizers());
        }
    }
}

interface ICompositeComparatorHasher<T> extends ICompositeComparator<T>, ICompositeHasher<T>, ComparatorHasher<T>{

}