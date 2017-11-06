package org.someth2say.taijitu.compare;

import org.someth2say.taijitu.config.interfaces.IComparisonCfg;

import java.util.ArrayList;

/**
 * @author Jordi Sola
 */
public class SimpleComparisonResult extends ComparisonResult {
    public SimpleComparisonResult(final IComparisonCfg comparisonConfigIface) {
        super(new ArrayList<>());
    }
}
