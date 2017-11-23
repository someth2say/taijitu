package org.someth2say.taijitu.compare.equality.stream;

import org.someth2say.taijitu.compare.equality.Equality;
import org.someth2say.taijitu.compare.result.Mismatch;
import org.someth2say.taijitu.util.Named;

import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Jordi Sola on 16/02/2017.
 */
//TODO: For equality purposes, those need not to be Named! Naming should be something external
public interface StreamEquality<T> extends Named, Equality<Stream<T>> {

    List<Mismatch> match(Stream<T> source, Object sourceId, Stream<T> target, Object targetId);

    @Override
    default List<Mismatch> differences(Stream<T> t1, Stream<T> t2) {
        return match(t1, 1, t2, 2);
    }
}
