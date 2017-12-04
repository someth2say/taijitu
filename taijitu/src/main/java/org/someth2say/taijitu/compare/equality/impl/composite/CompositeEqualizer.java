package org.someth2say.taijitu.compare.equality.impl.composite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.compare.equality.impl.value.JavaObject;

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
public class CompositeEqualizer<T> extends AbstractCompositeEquality implements ICompositeEqualizer<T> {
    private static final Logger logger = LoggerFactory.getLogger(CompositeEqualizer.class);

    @Override
	public Logger getLogger() {
        return logger;
    }

    protected CompositeEqualizer(List<ExtractorAndEquality> eaes) {
        super(eaes);
    }

    protected <V> CompositeEqualizer(Function<T, V> extractor, Equalizer<V> equalizer) {
        this(Collections.singletonList(new ExtractorAndEquality<>(extractor, equalizer)));
    }

    public static class Builder<T> {
        private List<ExtractorAndEquality> eaes = new ArrayList<>();

        public <V> Builder<T> addComponent(Function<T, V> extractor) {
            return addComponent(extractor, new JavaObject<>());
        }

        public <V> Builder<T> addComponent(Function<T, V> extractor, Equalizer<V> equalizer) {
            ExtractorAndEquality<T, V, Equalizer<V>> eae = new ExtractorAndEquality<>(extractor, equalizer);
            eaes.add(eae);
            return this;
        }

        public CompositeEqualizer<T> build() {
            return new CompositeEqualizer<>(eaes);
        }
    }
}
