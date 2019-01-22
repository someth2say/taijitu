package org.someth2say.taijitu.compare.equality.impl.composite;

import org.someth2say.taijitu.compare.equality.aspects.external.ComparatorHasher;
import org.someth2say.taijitu.compare.equality.impl.partial.IndirectComparatorHasher;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CompositeComparatorHasher<T> extends Composite<T,ComparatorHasher<T>> implements ICompositeComparatorHasher<T,ComparatorHasher<T>> {

	protected CompositeComparatorHasher(List<ComparatorHasher<T>> components) {
        super(components);
    }

    public static class Builder<T> {
        private final List<ComparatorHasher<T>> equalities = new ArrayList<>();

        public Builder<T> addComponent(ComparatorHasher<T> equalizer) {
            equalities.add(equalizer);
            return this;
        }

        public <R> Builder<T> addComponent(Function<T,R> extractor, ComparatorHasher<R> delegate) {
            return addComponent(new IndirectComparatorHasher<>(extractor, delegate));
        }


        public CompositeComparatorHasher<T> build() {
            return new CompositeComparatorHasher<>(equalities);
        }
    }
}

interface ICompositeComparatorHasher<T, E extends ComparatorHasher<T>>
        extends ICompositeComparator<T,E>, ICompositeHasher<T,E>, ComparatorHasher<T> {
}