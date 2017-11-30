package org.someth2say.taijitu.compare.equality.composite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.compare.equality.CategorizerEquality;
import org.someth2say.taijitu.compare.equality.ComparableCategorizerEquality;
import org.someth2say.taijitu.compare.equality.Equality;
import org.someth2say.taijitu.compare.equality.value.JavaComparable;
import org.someth2say.taijitu.compare.equality.value.JavaObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CompositeComparableCategorizerEquality<T> extends AbstractCompositeEquality implements ICompositeComparable<T>, ICompositeCategorizer<T>, ICompositeEquality<T> {
    private static final Logger logger = LoggerFactory.getLogger(CompositeComparableCategorizerEquality.class);

    public Logger getLogger() {
        return logger;
    }

    protected CompositeComparableCategorizerEquality(List<ExtractorAndEquality> extractorsAndEqualities) {
        super(extractorsAndEqualities);
    }

    public static class Builder<T> {
        private List<ExtractorAndEquality> eaes = new ArrayList<>();

        public <V extends Comparable<V>> Builder<T> addComponent(Function<T, V> extractor) {
            return addComponent(extractor, new JavaComparable<>());
        }

        public <V> Builder<T> addComponent(Function<T, V> extractor, ComparableCategorizerEquality<V> equality) {
            ExtractorAndEquality<T, V, Equality<V>> eae = new ExtractorAndEquality<>(extractor, equality);
            eaes.add(eae);
            return this;
        }

        public CompositeComparableCategorizerEquality<T> build() {
            return new CompositeComparableCategorizerEquality<>(eaes);
        }
    }
}
