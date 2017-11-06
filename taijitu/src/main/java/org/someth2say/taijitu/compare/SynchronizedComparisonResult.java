package org.someth2say.taijitu.compare;

import java.util.ArrayList;
import java.util.Collections;

import org.someth2say.taijitu.config.interfaces.IComparisonCfg;

/**
 * @author Jordi Sola
 */
public class SynchronizedComparisonResult<T> extends ComparisonResult<T> {
   public SynchronizedComparisonResult() {
        super(Collections.synchronizedCollection(new ArrayList<>()));
    }
}
