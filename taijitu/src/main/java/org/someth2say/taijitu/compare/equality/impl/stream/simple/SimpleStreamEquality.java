package org.someth2say.taijitu.compare.equality.impl.stream.simple;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.someth2say.taijitu.compare.equality.aspects.external.Equality;
import org.someth2say.taijitu.compare.equality.impl.stream.StreamEquality;
import org.someth2say.taijitu.compare.result.Mismatch;
import org.someth2say.taijitu.compare.result.Missing;
import org.someth2say.taijitu.util.StreamUtil;

/**
 * Created by Jordi Sola on 02/03/2017.
 */
public class SimpleStreamEquality<T> implements StreamEquality<T> {

    private final Equality<T> equality;

    public SimpleStreamEquality(Equality<T> equality) {
        this.equality = equality;
    }

    @Override
    public List<Mismatch<?>> underlyingDiffs(Stream<T> source, Stream<T> target) {
        return compare(source, target, equality);
    }

    public List<Mismatch<?>> compare(Stream<T> source, Stream<T> target, Equality<T> equality) {
        return StreamUtil.biMapTail(source, target,equality::asDifference, equality::asMissing)
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

}
