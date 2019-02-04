package org.someth2say.taijitu.compare.equality.impl.partial;

import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.compare.explain.Explainer;

import java.util.function.Function;

public class IndirectEqualizer<T,R> extends Indirect<T, R> implements IIndirectEqualizer<T,R> {

    public IndirectEqualizer(Function<T, R> extractor, Equalizer<R> delegate) {
        super(extractor, delegate);
    }

    @Override
    public String toString() {
        return "*"+getDelegate().toString();
    }
}
interface IIndirectEqualizer<T,R> extends Equalizer<T>, IIndirect<T, R>, Explainer<T> {

    @Override
    default boolean areEquals(T equalized1, T equalized2) {
        return getDelegate().areEquals(getExtractor().apply(equalized1), getExtractor().apply(equalized2));
    }

/*    @Override
    default Stream<Difference> explain(T equalized1, T equalized2) {
        return getDelegate().explain(getExtractor().apply(equalized1), getExtractor().apply(equalized2));
    }*/

}