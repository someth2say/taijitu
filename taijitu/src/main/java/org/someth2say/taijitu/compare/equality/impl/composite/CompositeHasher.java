package org.someth2say.taijitu.compare.equality.impl.composite;

import org.someth2say.taijitu.compare.equality.aspects.external.Hasher;
import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.compare.equality.impl.value.JavaObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CompositeHasher<T> extends AbstractCompositeEquality implements ICompositeHasher<T>, ICompositeEqualizer<T> {

    protected CompositeHasher(List<ExtractorAndEquality> extractorsAndEqualities) {
        super(extractorsAndEqualities);
    }

    public static class Builder<T> {
        private final List<ExtractorAndEquality> eaes = new ArrayList<>();

        public <V> Builder<T> addComponent(Function<T, V> extractor) {
            return addComponent(extractor, new JavaObject<>());
        }

        public <V> Builder<T> addComponent(Function<T, V> extractor, Hasher<V> equality) {
            ExtractorAndEquality<T, V, Equalizer<V>> eae = new ExtractorAndEquality<>(extractor, equality);
            eaes.add(eae);
            return this;
        }

        public CompositeHasher<T> build() {
            return new CompositeHasher<>(eaes);
        }
    }
}
