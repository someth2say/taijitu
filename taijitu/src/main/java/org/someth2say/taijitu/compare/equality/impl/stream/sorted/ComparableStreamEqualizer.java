package org.someth2say.taijitu.compare.equality.impl.stream.sorted;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.compare.equality.aspects.external.Comparator;
import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.compare.equality.impl.stream.StreamEqualizer;
import org.someth2say.taijitu.compare.result.Difference;
import org.someth2say.taijitu.util.StreamUtil;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * Created by Jordi Sola on 02/03/2017.
 */
public class ComparableStreamEqualizer<T> implements StreamEqualizer<T> {

    private static final Logger logger = LoggerFactory.getLogger(ComparableStreamEqualizer.class);
    private final Equalizer<T> equalizer;
    private final Comparator<T> categorizer;

    public ComparableStreamEqualizer(Equalizer<T> equalizer, Comparator<T> categorizer) {
        this.equalizer = equalizer;
        this.categorizer = categorizer;
    }

    @Override
    public Stream<Difference<?>> underlyingDiffs(Stream<T> source, Stream<T> target) {
        return compare(source, target, categorizer, equalizer);
    }

    public static <T> Stream<Difference<?>> compare(Stream<T> source, Stream<T> target, Comparator<T> comparator, Equalizer<T> equalizer) {
        Stream<Difference<?>> differenceStream = StreamUtil.comparingBiMap(source, target, comparator::compare, equalizer::asUnequal, comparator::asMissing);
        return differenceStream.filter(Objects::nonNull);

    }

}
