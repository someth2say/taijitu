package org.someth2say.taijitu.compare;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Jordi Sola
 */
public class SynchronizedComparisonResult extends ComparisonResult {
   public SynchronizedComparisonResult(final ComparisonConfigIface comparisonConfigIface) {
        super(comparisonConfigIface, Collections.synchronizedCollection(new ArrayList<>()), new ArrayList<>());
    }
}
