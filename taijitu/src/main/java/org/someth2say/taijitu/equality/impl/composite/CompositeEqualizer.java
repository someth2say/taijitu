package org.someth2say.taijitu.equality.impl.composite;

import org.someth2say.taijitu.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.equality.impl.delegating.DelegatingEqualizer;

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
public class CompositeEqualizer<T> implements ICompositeEqualizer<T> {

    private final List<? extends Equalizer<T>> equalizers;

    public CompositeEqualizer(List<? extends Equalizer<T>> equalizers) {
        this.equalizers = equalizers;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()
                + equalizers.stream().map(Equalizer::toString).collect(Collectors.joining(",","(",")"));
    }

    public List<? extends Equalizer<T>> getEqualizers() {
        return equalizers;
    }

    public static class Builder<T> {
        private final List<Equalizer<T>> equalizers = new ArrayList<>();

        public Builder<T> addEqualizer(Equalizer<T> equalizer) {
            equalizers.add(equalizer);
            return this;
        }


        public <R> Builder<T> addEqualizer(Function<T, R> extractor, Equalizer<R> delegate) {
            Equalizer<T> delegatingEqualizer = new DelegatingEqualizer<>(extractor, delegate);
            return addEqualizer(delegatingEqualizer);
        }


        public CompositeEqualizer<T> build() {
            return new CompositeEqualizer<>(getEqualizers());
        }

        protected List<? extends Equalizer<T>> getEqualizers() {
            return equalizers;
        }
    }

}

interface ICompositeEqualizer<T> extends Equalizer<T> {

    default boolean areEquals(T first, T second) {
        return getEqualizers().parallelStream()
                .allMatch(equalizer -> equalizer.areEquals(first, second));
    }

    List<? extends Equalizer<T>> getEqualizers();
}