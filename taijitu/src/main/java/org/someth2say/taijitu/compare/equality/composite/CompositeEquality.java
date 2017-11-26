package org.someth2say.taijitu.compare.equality.composite;

import org.someth2say.taijitu.compare.equality.Equality;
import org.someth2say.taijitu.compare.equality.composite.eae.ExtractorAndEquality;
import org.someth2say.taijitu.compare.equality.value.JavaObject;
import org.someth2say.taijitu.compare.result.Mismatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This is the simplest class of CompositeEquality. It assumes that:
 * <OL><LI>Both sides of equality are the same type and</LI>
 * <li>Both sides of equality are using the same extractors</li></OL>
 *
 * @param <T>
 */
public class CompositeEquality<T> extends AbstractCompositeEquality<T, ExtractorAndEquality<T, ?>> implements Equality<T> {

    public CompositeEquality(List<ExtractorAndEquality<T, ?>> eaes) {
        super(eaes);
    }

    public <V> CompositeEquality(Function<T, V> extractor, Equality<V> equality) {
        super(Collections.singletonList(new ExtractorAndEquality<>(extractor, equality)));
    }

    @Override
    public boolean equals(T first, T second) {
        return getExtractorsAndEqualities().stream().allMatch(eae -> valueEquals(first, second, eae));
    }

    @Override
    public List<Mismatch> differences(T t1, T t2) {
        return getExtractorsAndEqualities().stream().map(eae -> difference(t1, t2, eae)).filter(Objects::nonNull).collect(Collectors.toList());
    }

    //TODO: This is not enough. Builder should be able to type-safe produce any kind of composite equalities.
    public static class Builder<T> {
        private List<ExtractorAndEquality<T, ?>> eaes = new ArrayList<>();

        public <V> Builder addComponent(Function<T, V> extractor) {
            return addComponent(extractor, new JavaObject<>());
        }

        public <V> Builder addComponent(Function<T, V> extractor, Equality<V> equality) {
            eaes.add(new ExtractorAndEquality<>(extractor, equality));
            return this;
        }

        public CompositeEquality<T> build() {
            return new CompositeEquality<>(eaes);
        }
    }
}
