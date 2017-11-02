package org.someth2say.taijitu.compare;

import org.someth2say.taijitu.config.interfaces.IComparisonCfg;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Jordi Sola
 */
public class SynchronizedComparisonResult extends ComparisonResult {
   public SynchronizedComparisonResult(final IComparisonCfg comparison) {
        super(comparison, Collections.synchronizedCollection(new ArrayList<>()), new ArrayList<>());
    }
}
