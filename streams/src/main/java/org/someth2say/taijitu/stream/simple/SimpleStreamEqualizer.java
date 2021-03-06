package org.someth2say.taijitu.stream.simple;

import org.someth2say.taijitu.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.equality.explain.Difference;
import org.someth2say.taijitu.equality.explain.Missing;
import org.someth2say.taijitu.equality.explain.Unequal;
import org.someth2say.taijitu.stream.StreamEqualizer;
import org.someth2say.taijitu.stream.StreamUtil;

import java.util.stream.Stream;


/**
 * The simplest stream equalized.
 * This equalizes assumes both streams will provide elements as they should be compared.
 * That is, this equalizer will compare first element from source stream against first element from target stream.
 * Then, second element from both streams; then third element from both; and so on.
 * If one of the stream is exhausted, all elements remaining in the other stream will be reported as missing entries.
 */
public class SimpleStreamEqualizer<TYPE> implements StreamEqualizer<TYPE> {

    private final Equalizer<TYPE> elementEqualizer;

    public SimpleStreamEqualizer(final Equalizer<TYPE> elementEqualizer) {
        this.elementEqualizer = elementEqualizer;
    }

    @Override
    public Stream<Difference<TYPE>> explain(final Stream<TYPE> source, final Stream<TYPE> target) {
        return StreamUtil
                .biMapTail(source, target,
                        (sourceElem, targetElem) -> new Unequal<TYPE>(elementEqualizer, sourceElem, targetElem),
                        element -> new Missing<>(elementEqualizer, element),
                        elementEqualizer::areEquals );
    }
}
