package org.someth2say.taijitu.compare.equality.impl.composite;

import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.compare.equality.impl.partial.IndirectEqualizer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * This is the simplest class of CompositeEqualizer. It assumes that:
 * <OL><LI>Both sides of equality are the same type and</LI>
 * <li>Both sides of equality are using the same extractors</li></OL>
 *
 * @param <T>
 */
public class CompositeEqualizer<T> extends Composite<T, Equalizer<T>> implements ICompositeEqualizer<T, Equalizer<T>> {

    public CompositeEqualizer(List<Equalizer<T>> components) {
        super(components);
    }

    public static class Builder<T> {
        private final List<Equalizer<T>> equalities = new ArrayList<>();

        public Builder<T> addComponent(Equalizer<T> equalizer) {
            equalities.add(equalizer);
            return this;
        }

        //TODO: First approach for equality hierarchy: Partial application by builder
        public <R> Builder<T> addComponent(Function<T, R> extractor, Equalizer<R> delegate) {
            return addComponent(new IndirectEqualizer<>(extractor, delegate));
        }

        public CompositeEqualizer<T> build() {
            return new CompositeEqualizer<>(equalities);
        }
    }

}

interface ICompositeEqualizer<T, E extends Equalizer<T>> extends IComposite<E>, Equalizer<T> {

    default boolean areEquals(T first, T second) {
        return getComponents().allMatch(equalizer -> equalizer.areEquals(first, second));
    }

/*
    default Stream<Difference> explain(T first, T second) {
        return getComponents().flatMap(e -> e.explain(first, second));
    }
*/
}