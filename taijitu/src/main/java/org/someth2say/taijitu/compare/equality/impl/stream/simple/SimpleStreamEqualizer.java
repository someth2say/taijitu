package org.someth2say.taijitu.compare.equality.impl.stream.simple;

import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.compare.equality.impl.stream.StreamEqualizer;
import org.someth2say.taijitu.compare.result.Difference;
import org.someth2say.taijitu.util.StreamUtil;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by Jordi Sola on 02/03/2017.
 */
public class SimpleStreamEqualizer<T> implements StreamEqualizer<T> {

    private final Equalizer<T> equalizer;

    public SimpleStreamEqualizer(Equalizer<T> equalizer) {
        this.equalizer = equalizer;
    }

    //TODO: This is creating the foundation for obtaining root causes for differences.
    // But this approach forbids tracking the causes in the equality tree.
    @Override
    public Stream<Difference> underlyingDiffs(Stream<T> source, Stream<T> target) {
        return StreamUtil
                .biMapTail(source, target,
                        equalizer::underlyingDiffs,
                        equalizer::asMissing)
                .flatMap(Function.identity());

    }

}
