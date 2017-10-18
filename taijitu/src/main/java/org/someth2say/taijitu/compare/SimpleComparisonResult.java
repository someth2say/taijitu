package org.someth2say.taijitu.compare;

import org.someth2say.taijitu.config.ComparisonConfig;

import java.util.ArrayList;

/**
 * @author Jordi Sola
 */
public class SimpleComparisonResult extends ComparisonResult {
    public SimpleComparisonResult(final ComparisonConfig comparisonConfig) {
        super(comparisonConfig, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }
}
