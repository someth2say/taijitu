package org.someth2say.taijitu.compare.equality.impl.composite;

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

    protected CompositeEqualizer(List<ExtractorAndEquality> eaes) {
        super(eaes);
    }

    protected <V> CompositeEqualizer(Function<T, V> extractor, Equalizer<? super V> equalizer) {
        this(Collections.singletonList(new ExtractorAndEquality<>(extractor, equalizer)));
    }

    public static class Builder<T> {
        private final List<ExtractorAndEquality> eaes = new ArrayList<>();

        @SuppressWarnings("unchecked")
        public <V> Builder<T> addComponent(Function<T, V> extractor) {
            return addComponent(extractor, (Equalizer<V>)JavaObject.EQUALITY);
        }

        public <V> Builder<T> addComponent(Function<T, V> extractor, Equalizer<? super V> equalizer) {
            ExtractorAndEquality<T, V, Equalizer<? super V>> eae = new ExtractorAndEquality<>(extractor, equalizer);
            eaes.add(eae);
            return this;
        }

        public CompositeEqualizer<T> build() {
            return new CompositeEqualizer<>(eaes);
        }
    }
}
