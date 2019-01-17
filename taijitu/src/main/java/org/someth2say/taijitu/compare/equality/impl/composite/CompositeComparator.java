package org.someth2say.taijitu.compare.equality.impl.composite;

import org.someth2say.taijitu.compare.equality.aspects.external.Comparator;
import org.someth2say.taijitu.compare.equality.impl.partial.PartialComparator;

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
public class CompositeComparator<T> extends Composite<T,Comparator<T>> implements ICompositeComparator<T,Comparator<T>>{


    public CompositeComparator(List<Comparator<T>> components) {
        super(components);
    }

    public static class Builder<T> {
        private final List<Comparator<T>> equalities = new ArrayList<>();

        public Builder<T> addComponent(Comparator<T> equalizer) {
            equalities.add(equalizer);
            return this;
        }

        public <R> Builder<T> addComponent(Function<T,R> extractor, Comparator<R> delegate) {
            return addComponent(new PartialComparator<>(extractor,delegate));
        }

        public CompositeComparator<T> build() {
            return new CompositeComparator<>(equalities);
        }
    }

}

interface ICompositeComparator<T, E extends Comparator<T>> extends ICompositeEqualizer<T, E>, Comparator<T> {

    @Override
    default int compare(T first, T second) {
        return this.getComponents().map(comparator -> comparator.compare(first, second)).filter(i -> i != 0).findFirst().orElse(0);
    }
}