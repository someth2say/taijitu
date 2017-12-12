package org.someth2say.taijitu.compare.equality.impl.composite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.compare.equality.aspects.external.Comparator;
import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.compare.equality.impl.value.JavaComparable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * This is the simplest class of CompositeEqualizer. It assumes that:
 * <OL><LI>Both sides of equality are the same type and</LI>
 * <li>Both sides of equality are using the same extractors</li></OL>
 *
 * @param <T>
 */
public class CompositeComparator<T> extends AbstractCompositeEquality implements ICompositeEqualizer<T>, ICompositeComparator<T> {
    private static final Logger logger = LoggerFactory.getLogger(CompositeComparator.class);

    @Override
	public Logger getLogger() {
        return logger;
    }

    protected CompositeComparator(List<ExtractorAndEquality> eaes) {
        super(eaes);
    }

    protected <V> CompositeComparator(Function<T, V> extractor, Equalizer<V> equalizer) {
        this(Collections.singletonList(new ExtractorAndEquality<>(extractor, equalizer)));
    }

    public static class Builder<T> {
        private final List<ExtractorAndEquality> eaes = new ArrayList<>();

        public <V extends Comparable<V>> Builder<T> addComponent(Function<T, V> extractor) {
            return addComponent(extractor, new JavaComparable<>());
        }

        public <V> Builder<T> addComponent(Function<T, V> extractor, Comparator<V> comparator) {
            ExtractorAndEquality<T, V, Equalizer<V>> eae = new ExtractorAndEquality<>(extractor, comparator);
            eaes.add(eae);
            return this;
        }

        public CompositeComparator<T> build() {
            return new CompositeComparator<>(eaes);
        }
    }

}
