package org.someth2say.taijitu.compare.equality.impl.composite;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;

public abstract class AbstractCompositeEquality<T> implements IComposite<T> {

    protected final List<ExtractorAndEquality<T, ?, ? extends Equalizer<?>>> extractorsAndEqualities;

    public AbstractCompositeEquality(List<ExtractorAndEquality<T, ?, ? extends Equalizer<?>>> eaes) {
        this.extractorsAndEqualities = eaes;
    }

    public Stream<ExtractorAndEquality<T, ?, ? extends Equalizer<?>>> getExtractorsAndEqualities() {
        return extractorsAndEqualities.stream();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "("
                + extractorsAndEqualities.stream().map(ExtractorAndEquality::toString).collect(Collectors.joining(","))
                + ")";
    }
}