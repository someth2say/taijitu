package org.someth2say.taijitu.strategy;

import org.someth2say.taijitu.ComparisonContext;
import org.someth2say.taijitu.compare.ComparisonResult;
import org.someth2say.taijitu.config.ComparisonConfig;
import org.someth2say.taijitu.source.ResultSetSource;
import org.someth2say.taijitu.tuple.ComparableTuple;
import org.someth2say.taijitu.util.Named;

import java.util.Iterator;

/**
 * Created by Jordi Sola on 16/02/2017.
 */
public interface ComparisonStrategy extends Named {
    ComparisonResult runComparison(Iterator<ComparableTuple> source, Iterator<ComparableTuple> target, ComparisonContext comparisonContext, ComparisonConfig comparisonConfig);
}
