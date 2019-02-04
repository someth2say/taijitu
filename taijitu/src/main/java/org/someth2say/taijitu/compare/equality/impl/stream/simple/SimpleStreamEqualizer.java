package org.someth2say.taijitu.compare.equality.impl.stream.simple;

import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.compare.equality.impl.stream.StreamEqualizer;
import org.someth2say.taijitu.compare.explain.Difference;
import org.someth2say.taijitu.compare.explain.Missing;
import org.someth2say.taijitu.compare.explain.Unequal;
import org.someth2say.taijitu.util.StreamUtil;

import java.util.Objects;
import java.util.stream.Stream;


/**
 * The simplest stream equalized.
 * This equalizes assumes both streams will provide elements as they should be compared.
 * That is, this equalizer will compare first element from source stream against first element from target stream.
 * Then, second element from both streams; then third element from both; and so on.
 * If one of the stream is exhausted, all elements remaining in the other stream will be reported as missing entries.
 */
public class SimpleStreamEqualizer<ELEMENT> implements StreamEqualizer<ELEMENT> {

    private final Equalizer<ELEMENT> elementEqualizer;

    public SimpleStreamEqualizer(Equalizer<ELEMENT> elementEqualizer) {
        this.elementEqualizer = elementEqualizer;
    }

    //TODO: This is creating the foundation for obtaining root causes for differences.
    // But this approach forbids tracking the causes in the equality tree.
    @Override
    public Stream<Difference> explain(Stream<ELEMENT> source, Stream<ELEMENT> target) {
        return StreamUtil
                .biMapTail(source, target,
                        (sourceElem, targetElem) -> elementEqualizer.areEquals(sourceElem, targetElem) ? null : new Unequal<>(elementEqualizer, sourceElem, targetElem),
                        element -> new Missing(elementEqualizer, element)) //Stupid type inference... if diamond operator is used, then the explain is Stream<Difference<ELEMENT>>, witch is incompatible with return type Stream<Difference>>
                .filter(Objects::nonNull);

    }
}
