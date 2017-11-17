package org.someth2say.taijitu.compare.equality.stream;

import org.someth2say.taijitu.compare.result.ComparisonResult;
import org.someth2say.taijitu.util.Named;

import java.util.stream.Stream;

/**
 * Created by Jordi Sola on 16/02/2017.
 */
//TODO: For equality purposes, those need not to be Named! Naming should be something external
public interface StreamEquality<T> extends Named {

    ComparisonResult<T> match(Stream<T> source, Object sourceId, Stream<T> target, Object targetId);

}
