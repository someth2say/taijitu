package org.someth2say.taijitu.strategy;

import org.someth2say.taijitu.ComparisonContext;
import org.someth2say.taijitu.compare.ComparisonResult;
import org.someth2say.taijitu.compare.equality.external.ExternalEquality;
import org.someth2say.taijitu.source.Source;
import org.someth2say.taijitu.tuple.ComparableTuple;
import org.someth2say.taijitu.util.Named;

/**
 * Created by Jordi Sola on 16/02/2017.
 */
public interface ComparisonStrategy extends Named {
    @Deprecated
    <T extends ComparableTuple> ComparisonResult<T> runComparison(Source<T> source, Source<T> target, ComparisonContext comparisonContext);

    <T> ComparisonResult<T> runExternalComparison(Source<T> source, Source<T> target, ExternalEquality<T> externalCategorizer, ExternalEquality<T> externalEquality);

}
