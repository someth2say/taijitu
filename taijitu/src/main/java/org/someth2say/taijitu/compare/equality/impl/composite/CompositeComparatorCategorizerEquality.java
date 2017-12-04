package org.someth2say.taijitu.compare.equality.composite;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.compare.equality.external.ComparatorCategorizerEquality;
import org.someth2say.taijitu.compare.equality.external.Equality;
import org.someth2say.taijitu.compare.equality.value.JavaComparable;

public class CompositeComparatorCategorizerEquality<T> extends AbstractCompositeEquality implements ICompositeCategorizerComparatorEquality<T> {
    private static final Logger logger = LoggerFactory.getLogger(CompositeComparatorCategorizerEquality.class);

    @Override
	public Logger getLogger() {
        return logger;
    }

	protected CompositeComparatorCategorizerEquality(List<ExtractorAndEquality> extractorsAndEqualities) {
        super(extractorsAndEqualities);
    }

    public static class Builder<T> {
        private List<ExtractorAndEquality> eaes = new ArrayList<>();

        public <V extends Comparable<V>> Builder<T> addComponent(Function<T, V> extractor) {
            return addComponent(extractor, new JavaComparable<>());
        }

        public <V> Builder<T> addComponent(Function<T, V> extractor, ComparatorCategorizerEquality<V> equality) {
            ExtractorAndEquality<T, V, Equality<V>> eae = new ExtractorAndEquality<>(extractor, equality);
            eaes.add(eae);
            return this;
        }

        public CompositeComparatorCategorizerEquality<T> build() {
            return new CompositeComparatorCategorizerEquality<>(eaes);
        }
    }
}
