package org.someth2say.taijitu.stream.sorted;

import org.someth2say.taijitu.equality.aspects.external.Comparator;
import org.someth2say.taijitu.equality.explain.Difference;
import org.someth2say.taijitu.equality.explain.Missing;
import org.someth2say.taijitu.equality.explain.Unequal;
import org.someth2say.taijitu.stream.StreamEqualizer;
import org.someth2say.taijitu.stream.StreamUtil;

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
public class ComparableStreamEqualizer<TYPE> implements StreamEqualizer<TYPE> {

    private final Comparator<TYPE> comparator;

    public ComparableStreamEqualizer(Comparator<TYPE> comparator) {
        this.comparator = comparator;
    }

    @Override
    public Stream<Difference<TYPE>> explain(Stream<TYPE> source, Stream<TYPE> target) {
        return compare(source, target, comparator);
    }

    /**
     * Short-cut method that performs stream comparison without actually creating the equalizer.
     *
     * @param source
     * @param target
     * @param comparator
     * @param <ELEMENT>
     * @return
     */
    public static <ELEMENT> Stream<Difference<ELEMENT>> compare(Stream<ELEMENT> source, Stream<ELEMENT> target, Comparator<ELEMENT> comparator) {
        return StreamUtil.comparingBiMapTail(source, target,
                comparator::compare,
                (sourceElem, targetElem) ->  new Unequal<ELEMENT>(comparator, sourceElem, targetElem),
                element -> new Missing<ELEMENT>(comparator, element),
                comparator::areEquals);
    }

}
