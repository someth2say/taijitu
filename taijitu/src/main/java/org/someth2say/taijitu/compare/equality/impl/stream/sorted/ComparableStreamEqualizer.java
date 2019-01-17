package org.someth2say.taijitu.compare.equality.impl.stream.sorted;

import org.someth2say.taijitu.compare.equality.aspects.external.Comparator;
import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.compare.equality.impl.stream.StreamEqualizer;
import org.someth2say.taijitu.compare.result.Difference;
import org.someth2say.taijitu.util.StreamUtil;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by Jordi Sola on 02/03/2017.
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
    public Stream<Difference> underlyingDiffs(Stream<T> source, Stream<T> target) {
        return compare(source, target, comparator, equalizer);
    }

    /**
     * Short-cut method that performs stream comparison without actually creating the equalizer.
     *
     * @param source
     * @param target
     * @param comparator
     * @param equalizer
     * @param <T>
     * @return
     */
    public static <T> Stream<Difference> compare(Stream<T> source, Stream<T> target, Comparator<T> comparator, Equalizer<T> equalizer) {
        return StreamUtil.comparingBiMap(source, target,
                    comparator::compare,
                    equalizer::underlyingDiffs,
                    comparator::asMissing)
                .flatMap(Function.identity());

    }

}
