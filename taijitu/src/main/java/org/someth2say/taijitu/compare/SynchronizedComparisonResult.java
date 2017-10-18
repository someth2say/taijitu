package org.someth2say.taijitu.compare;

import org.someth2say.taijitu.config.ComparisonConfig;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Jordi Sola
 */
public class SynchronizedComparisonResult extends ComparisonResult {
   public SynchronizedComparisonResult(final ComparisonConfig comparisonConfig) {
        super(comparisonConfig, Collections.synchronizedCollection(new ArrayList<>()), new ArrayList<>(), new ArrayList<>());
    }
}
