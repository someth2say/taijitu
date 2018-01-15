package org.someth2say.taijitu.compare.equality.impl.composite;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.someth2say.taijitu.compare.equality.aspects.external.ComparatorHasher;
import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.compare.equality.impl.value.JavaComparable;

public class CompositeComparatorHasher<T> extends AbstractCompositeEquality implements ICompositeHasherComparator<T> {

	protected CompositeComparatorHasher(List<ExtractorAndEquality> extractorsAndEqualities) {
        super(extractorsAndEqualities);
    }

    public static class Builder<T> {
        private final List<ExtractorAndEquality> eaes = new ArrayList<>();

        public <V extends Comparable<V>> Builder<T> addComponent(Function<T, V> extractor) {
            return addComponent(extractor, new JavaComparable<>());
        }

        public <V> Builder<T> addComponent(Function<T, V> extractor, ComparatorHasher<? super V> equality) {
            ExtractorAndEquality<T, V, Equalizer<? super V>> eae = new ExtractorAndEquality<>(extractor, equality);
            eaes.add(eae);
            return this;
        }

        public CompositeComparatorHasher<T> build() {
            return new CompositeComparatorHasher<>(eaes);
        }
    }
}
