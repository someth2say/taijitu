package org.someth2say.taijitu.compare.equality.impl.composite;

import java.util.stream.Stream;

import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;

public interface IComposite<T> {
    Stream<ExtractorAndEquality<T,?,? extends Equalizer<?>>> getExtractorsAndEqualities();
}
