package org.someth2say.taijitu.compare;

import java.util.ArrayList;

/**
 * @author Jordi Sola
 */
public class SimpleComparisonResult extends ComparisonResult {
    public SimpleComparisonResult(final ComparisonConfigIface comparisonConfigIface) {
        super(comparisonConfigIface, new ArrayList<>(), new ArrayList<>());
    }
}
