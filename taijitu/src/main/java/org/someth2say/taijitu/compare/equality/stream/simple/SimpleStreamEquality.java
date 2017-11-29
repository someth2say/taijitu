package org.someth2say.taijitu.compare.equality.stream.simple;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.someth2say.taijitu.compare.equality.ComparableCategorizerEquality;
import org.someth2say.taijitu.compare.equality.Equality;
import org.someth2say.taijitu.compare.equality.stream.AbstractStreamEquality;
import org.someth2say.taijitu.compare.result.Difference;
import org.someth2say.taijitu.compare.result.Mismatch;
import org.someth2say.taijitu.compare.result.Missing;
import org.someth2say.taijitu.util.StreamUtil;

/**
 * Created by Jordi Sola on 02/03/2017.
 */
public class SimpleStreamEquality<T> extends AbstractStreamEquality<T> {

    public SimpleStreamEquality(Equality<T> equality, ComparableCategorizerEquality<T> categorizer) {
        super(equality, null);
    }

    @Override
    public List<Mismatch<?>> underlyingDiffs(Stream<T> source, Stream<T> target) {
        return compare(source, target, getEquality());
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
