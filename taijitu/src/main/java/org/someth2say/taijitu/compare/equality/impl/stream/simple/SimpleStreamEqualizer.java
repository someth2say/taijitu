package org.someth2say.taijitu.compare.equality.impl.stream.simple;

import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.compare.equality.impl.stream.StreamEqualizer;
import org.someth2say.taijitu.compare.result.Difference;
import org.someth2say.taijitu.compare.result.Unequal;
import org.someth2say.taijitu.compare.result.Missing;
import org.someth2say.taijitu.util.StreamUtil;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Jordi Sola on 02/03/2017.
 */
public class SimpleStreamEqualizer<T> implements StreamEqualizer<T> {

    private final Equalizer<T> equalizer;

    public SimpleStreamEqualizer(Equalizer<T> equalizer) {

        this.equalizer = equalizer;
    }

    @Override
    public List<Difference<?>> underlyingDiffs(Stream<T> source, Stream<T> target) {
        return compare(source, target, equalizer);
    }

    public List<Difference<?>> compare(Stream<T> source, Stream<T> target, Equalizer<T> equalizer) {
        return StreamUtil.biMapTail(source, target,
                (t, t2) -> differenceOrNull(equalizer, t, t2),
                t -> new Missing<>(equalizer, t)).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private static <T> Unequal<T> differenceOrNull(Equalizer<T> equalizer, T sourceRecord, T targetRecord) {
        List<Difference<?>> differences = equalizer.underlyingDiffs(sourceRecord, targetRecord);
        if (differences != null && !differences.isEmpty()) {
            return new Unequal<>(equalizer, sourceRecord, targetRecord, differences);
        }
        return null;
    }

}
