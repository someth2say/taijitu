package org.someth2say.taijitu.compare.equality.composite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.compare.equality.external.ComparatorEquality;
import org.someth2say.taijitu.compare.equality.external.Equality;
import org.someth2say.taijitu.compare.equality.value.JavaComparable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * This is the simplest class of CompositeEquality. It assumes that:
 * <OL><LI>Both sides of equality are the same type and</LI>
 * <li>Both sides of equality are using the same extractors</li></OL>
 *
 * @param <T>
 */
public class CompositeComparatorEquality<T> extends AbstractCompositeEquality implements ICompositeEquality<T>, ICompositeComparator<T> {
    private static final Logger logger = LoggerFactory.getLogger(CompositeComparatorEquality.class);

    public Logger getLogger() {
        return logger;
    }

    protected CompositeComparatorEquality(List<ExtractorAndEquality> eaes) {
        super(eaes);
    }

    protected <V> CompositeComparatorEquality(Function<T, V> extractor, Equality<V> equality) {
        this(Collections.singletonList(new ExtractorAndEquality<>(extractor, equality)));
    }

    public static class Builder<T> {
        private List<ExtractorAndEquality> eaes = new ArrayList<>();

        public <V extends Comparable<V>> Builder<T> addComponent(Function<T, V> extractor) {
            return addComponent(extractor, new JavaComparable<>());
        }

        public <V> Builder<T> addComponent(Function<T, V> extractor, ComparatorEquality<V> equality) {
            ExtractorAndEquality<T, V, Equality<V>> eae = new ExtractorAndEquality<>(extractor, equality);
            eaes.add(eae);
            return this;
        }

        public CompositeComparatorEquality<T> build() {
            return new CompositeComparatorEquality<>(eaes);
        }
    }

}
