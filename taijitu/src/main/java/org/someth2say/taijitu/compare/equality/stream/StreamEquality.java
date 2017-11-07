package org.someth2say.taijitu.compare.equality.stream;

import org.someth2say.taijitu.compare.result.ComparisonResult;
import org.someth2say.taijitu.source.Source;
import org.someth2say.taijitu.util.Named;

/**
 * Created by Jordi Sola on 16/02/2017.
 */
public interface StreamEquality<T> extends Named {

    ComparisonResult<T> runExternalComparison(Source<T> source, Source<T> target);

}
