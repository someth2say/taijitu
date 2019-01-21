package org.someth2say.taijitu.compare.equality.impl.partial;

import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.compare.result.Difference;

import java.util.function.Function;
import java.util.stream.Stream;

public class PartialEqualizer<T,R> extends Partial<T, R> implements IPartialEqualizer<T,R> {

    public PartialEqualizer(Function<T, R> extractor, Equalizer<R> delegate) {
        super(extractor, delegate);
    }

    @Override
    public String toString() {
        return "*"+getDelegate().toString();
    }
}
interface IPartialEqualizer<T,R> extends Equalizer<T>, IPartial<T, R> {

    @Override
    default boolean areEquals(T equalized1, T equalized2) {
        return getDelegate().areEquals(getExtractor().apply(equalized1), getExtractor().apply(equalized2));
    }

    @Override
    default Stream<Difference> underlyingDiffs(T equalized1, T equalized2) {
        return getDelegate().underlyingDiffs(getExtractor().apply(equalized1), getExtractor().apply(equalized2));
    }

}