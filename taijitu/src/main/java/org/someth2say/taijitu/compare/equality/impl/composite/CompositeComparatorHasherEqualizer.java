package org.someth2say.taijitu.compare.equality.impl.composite;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.compare.equality.aspects.external.ComparatorHasherEqualizer;
import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.compare.equality.impl.value.JavaComparable;

public class CompositeComparatorHasherEqualizer<T> extends AbstractCompositeEquality implements ICompositeHasherComparatorEqualizer<T> {
    private static final Logger logger = LoggerFactory.getLogger(CompositeComparatorHasherEqualizer.class);

    @Override
	public Logger getLogger() {
        return logger;
    }

	protected CompositeComparatorHasherEqualizer(List<ExtractorAndEquality> extractorsAndEqualities) {
        super(extractorsAndEqualities);
    }

    public static class Builder<T> {
        private final List<ExtractorAndEquality> eaes = new ArrayList<>();

        public <V extends Comparable<V>> Builder<T> addComponent(Function<T, V> extractor) {
            return addComponent(extractor, new JavaComparable<>());
        }

        public <V> Builder<T> addComponent(Function<T, V> extractor, ComparatorHasherEqualizer<V> equality) {
            ExtractorAndEquality<T, V, Equalizer<V>> eae = new ExtractorAndEquality<>(extractor, equality);
            eaes.add(eae);
            return this;
        }

        public CompositeComparatorHasherEqualizer<T> build() {
            return new CompositeComparatorHasherEqualizer<>(eaes);
        }
    }
}