package org.someth2say.taijitu.compare.equality.impl.composite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.compare.equality.aspects.external.HasherEqualizer;
import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.compare.equality.impl.value.JavaObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CompositeHasherEqualizer<T> extends AbstractCompositeEquality implements ICompositeHasherEqualizer<T>, ICompositeEqualizer<T> {
    private static final Logger logger = LoggerFactory.getLogger(CompositeHasherEqualizer.class);

    @Override
	public Logger getLogger() {
        return logger;
    }

    protected CompositeHasherEqualizer(List<ExtractorAndEquality> extractorsAndEqualities) {
        super(extractorsAndEqualities);
    }

    public static class Builder<T> {
        private final List<ExtractorAndEquality> eaes = new ArrayList<>();

        public <V> Builder<T> addComponent(Function<T, V> extractor) {
            return addComponent(extractor, new JavaObject<>());
        }

        public <V> Builder<T> addComponent(Function<T, V> extractor, HasherEqualizer<V> equality) {
            ExtractorAndEquality<T, V, Equalizer<V>> eae = new ExtractorAndEquality<>(extractor, equality);
            eaes.add(eae);
            return this;
        }

        public CompositeHasherEqualizer<T> build() {
            return new CompositeHasherEqualizer<>(eaes);
        }
    }
}
