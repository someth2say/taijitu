package org.someth2say.taijitu.compare.equality.impl.stream.sorted;

import org.someth2say.taijitu.compare.equality.aspects.external.Comparator;
import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.compare.equality.impl.stream.StreamEqualizer;
import org.someth2say.taijitu.compare.explain.Difference;
import org.someth2say.taijitu.compare.explain.Missing;
import org.someth2say.taijitu.compare.explain.Unequal;
import org.someth2say.taijitu.util.StreamUtil;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * An evolution from SimpleStreamEqualizer.
 * Its main advantage is that ComparableStreamEqualizer uses a Comparator to identify gaps of missing elements in streams.
 * Before testing element for equality, they are compared for order. If both elements are equivalent in order,
 * then Equalizer is used o check their equality. If one element is "lesser" than the other, this element is considered
 * not having a matching element on the other stream, and hence it is reported as Missing.
 * <p>
 * This comparison scheme implies stream elements are ordered given the order defined by the comparator.
 */
public class ComparableStreamEqualizer<T> implements StreamEqualizer<T> {

    private final Equalizer<T> equalizer;
    private final Comparator<T> comparator;

    public ComparableStreamEqualizer(Comparator<T> comparator) {
        this.equalizer = comparator;
        this.comparator = comparator;
    }

    public ComparableStreamEqualizer(Equalizer<T> equalizer, Comparator<T> comparator) {
        this.equalizer = equalizer;
        this.comparator = comparator;
    }

    @Override
    public Stream<Difference> explain(Stream<T> source, Stream<T> target) {
        return compare(source, target, comparator, equalizer);
    }

    /**
     * Short-cut method that performs stream comparison without actually creating the equalizer.
     *
     * @param source
     * @param target
     * @param comparator
     * @param elementEqualizer
     * @param <ELEMENT>
     * @return
     */
    public static <ELEMENT> Stream<Difference> compare(Stream<ELEMENT> source, Stream<ELEMENT> target, Comparator<ELEMENT> comparator, Equalizer<ELEMENT> elementEqualizer) {
        return StreamUtil.comparingBiMap(source, target,
                comparator::compare,
                (sourceElem, targetElem) -> elementEqualizer.areEquals(sourceElem, targetElem) ? null : new Unequal<>(elementEqualizer, sourceElem, targetElem),
                element -> new Missing(comparator, element))
                .filter(Objects::nonNull);
    }

}
