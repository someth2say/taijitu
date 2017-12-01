package org.someth2say.taijitu.compare.equality.stream.simple;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.someth2say.taijitu.compare.equality.external.Equality;
import org.someth2say.taijitu.compare.result.Difference;
import org.someth2say.taijitu.compare.result.Mismatch;
import org.someth2say.taijitu.compare.result.Missing;
import org.someth2say.taijitu.util.StreamUtil;

/**
 * Created by Jordi Sola on 02/03/2017.
 */
public class SimpleStreamEquality<T> implements org.someth2say.taijitu.compare.equality.stream.StreamEquality<T> {

    private final Equality<T> equality;

    public SimpleStreamEquality(Equality<T> equality) {

        this.equality = equality;
    }

    @Override
    public List<Mismatch<?>> underlyingDiffs(Stream<T> source, Stream<T> target) {
        return compare(source, target, equality);
    }

    public List<Mismatch<?>> compare(Stream<T> source, Stream<T> target, Equality<T> equality) {
        return StreamUtil.biMapTail(source, target,
                (t, t2) -> differenceOrNull(equality, t, t2),
                t -> new Missing<>(equality, t)).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private static <T> Difference<T> differenceOrNull(Equality<T> equality, T sourceRecord, T targetRecord) {
        List<Mismatch<?>> differences = equality.underlyingDiffs(sourceRecord, targetRecord);
        if (differences != null && !differences.isEmpty()) {
            return new Difference<>(equality, sourceRecord, targetRecord, differences);
        }
        return null;
    }

}
