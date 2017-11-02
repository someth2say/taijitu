package org.someth2say.taijitu.strategy;

import org.someth2say.taijitu.ComparisonContext;
import org.someth2say.taijitu.compare.ComparisonResult;
import org.someth2say.taijitu.config.interfaces.IComparisonCfg;
import org.someth2say.taijitu.source.Source;
import org.someth2say.taijitu.util.Named;

/**
 * Created by Jordi Sola on 16/02/2017.
 */
public interface ComparisonStrategy extends Named {
    ComparisonResult runComparison(Source source, Source target, ComparisonContext comparisonContext, IComparisonCfg comparisonConfigIface);
}
