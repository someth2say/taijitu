package org.someth2say.taijitu.equality.impl.composite;

import org.someth2say.taijitu.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.equality.aspects.external.Hasher;
import org.someth2say.taijitu.equality.impl.delegating.DelegatingHasher;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CompositeHasher<T> extends CompositeEqualizer<T> implements ICompositeHasher<T> {

    private final List<? extends Hasher<T>> hashers;

    public CompositeHasher(List<? extends Hasher<T>> hashers) {
        this(hashers,hashers);
    }

    private CompositeHasher(List<? extends Hasher<T>> hashers, List<? extends Equalizer<T>> equalizers) {
        super(equalizers);
        this.hashers = hashers;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()
                + hashers.stream().map(Equalizer::toString).collect(Collectors.joining(",","(",")"));
    }

    public List<? extends Hasher<T>> getHashers() {
        return hashers;
    }

    public static class Builder<T> extends CompositeEqualizer.Builder<T> {
        private final List<Hasher<T>> hashers = new ArrayList<>();

        public Builder<T> addHasher(Hasher<T> hasher) {
            addEqualizer(hasher);
            hashers.add(hasher);
            return this;
        }


        public <R> Builder<T> addHasher(Function<T, R> extractor, Hasher<R> delegate) {
            Hasher<T> delegatingHasher = new DelegatingHasher<>(extractor, delegate);
            return addHasher(delegatingHasher);
        }


        public CompositeHasher<T> build() {
            return new CompositeHasher<>(getHashers(), getEqualizers());
        }

        protected List<Hasher<T>> getHashers() {
            return hashers;
        }

    }
}

interface ICompositeHasher<T> extends Hasher<T>, ICompositeEqualizer<T> {

    @Override
    default int hash(T obj) {
        int result = 1;
        //TODO: getHashers produce an ordered stream (as per contract). So it should be safe reduce the stream
        for (Hasher<T> e : getHashers()) {
            result = 31 * result + e.hash(obj);
        }
        return result;
    }

    List<? extends Hasher<T>> getHashers();

}