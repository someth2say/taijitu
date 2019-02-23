package org.someth2say.taijitu.equality.impl.composite;

import org.someth2say.taijitu.equality.aspects.external.Hasher;
import org.someth2say.taijitu.equality.impl.delegating.DelegatingHasher;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class CompositeHasher<T> extends Composite<T, Hasher<T>> implements ICompositeHasher<T, Hasher<T>> {

    public CompositeHasher(List<Hasher<T>> hashers) {
        super(hashers);
    }

    public static class Builder<T> {
        private final List<Hasher<T>> equalities = new ArrayList<>();

        public Builder<T> addComponent(Hasher<T> hasher) {
            equalities.add(hasher);
            return this;
        }

        public <R> Builder<T> addComponent(Function<T, R> extractor, Hasher<R> delegate) {
            return addComponent(new DelegatingHasher<>(extractor, delegate));
        }

        public CompositeHasher<T> build() {
            return new CompositeHasher<>(equalities);
        }
    }
}

interface ICompositeHasher<T, E extends Hasher<T>> extends Hasher<T>, ICompositeEqualizer<T, E> {

    @Override
    default int hash(T obj) {
        int result = 1;
        for (Iterator<E> it = getComponents().iterator(); it.hasNext(); ) {
            result = 31 * result + it.next().hash(obj);
        }
        return result;
    }
}