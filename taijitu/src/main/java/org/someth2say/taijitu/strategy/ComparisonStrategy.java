package org.someth2say.taijitu.strategy;

import org.someth2say.taijitu.ComparisonRuntime;
import org.someth2say.taijitu.compare.ComparisonResult;
import org.someth2say.taijitu.config.ComparisonConfig;
import org.someth2say.taijitu.database.ResultSetIterator;
import org.someth2say.taijitu.util.Named;

/**
 * Created by Jordi Sola on 16/02/2017.
 */
public interface ComparisonStrategy extends Named {
    ComparisonResult runComparison(ResultSetIterator source, ResultSetIterator target, ComparisonRuntime comparisonRuntime, ComparisonConfig comparisonConfig);
}
