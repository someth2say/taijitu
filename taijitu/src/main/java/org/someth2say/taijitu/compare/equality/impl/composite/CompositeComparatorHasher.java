package org.someth2say.taijitu.compare.equality.impl.composite;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.compare.equality.aspects.external.ComparatorHasher;
import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.compare.equality.impl.value.JavaComparable;

public class CompositeComparatorHasher<T> extends AbstractCompositeEquality implements ICompositeHasherComparator<T> {
    private static final Logger logger = LoggerFactory.getLogger(CompositeComparatorHasher.class);

    @Override
	public Logger getLogger() {
        return logger;
    }

	protected CompositeComparatorHasher(List<ExtractorAndEquality> extractorsAndEqualities) {
        super(extractorsAndEqualities);
    }

    public static class Builder<T> {
        private final List<ExtractorAndEquality> eaes = new ArrayList<>();

        public <V extends Comparable<V>> Builder<T> addComponent(Function<T, V> extractor) {
            return addComponent(extractor, new JavaComparable<>());
        }

        public <V> Builder<T> addComponent(Function<T, V> extractor, ComparatorHasher<V> equality) {
            ExtractorAndEquality<T, V, Equalizer<V>> eae = new ExtractorAndEquality<>(extractor, equality);
            eaes.add(eae);
            return this;
        }

        public CompositeComparatorHasher<T> build() {
            return new CompositeComparatorHasher<>(eaes);
        }
    }
}
