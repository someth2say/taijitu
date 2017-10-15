package org.someth2say.taijitu.strategy;

import org.someth2say.taijitu.ComparisonRuntime;
import org.someth2say.taijitu.compare.ComparableTuple;
import org.someth2say.taijitu.compare.ComparisonResult;
import org.someth2say.taijitu.config.ComparisonConfig;
import org.someth2say.taijitu.query.ResultSetIterator;
import org.someth2say.taijitu.util.Named;

/**
 * Created by Jordi Sola on 16/02/2017.
 */
public interface ComparisonStrategy extends Named {
    <T extends ComparableTuple> ComparisonResult runComparison(ResultSetIterator<T> source, ResultSetIterator<T> target, ComparisonRuntime comparisonRuntime, ComparisonConfig comparisonConfig);
}
