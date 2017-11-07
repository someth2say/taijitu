package org.someth2say.taijitu.compare.result;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Jordi Sola
 */
public class SynchronizedComparisonResult<T> extends ComparisonResult<T> {
   public SynchronizedComparisonResult() {
        super(Collections.synchronizedCollection(new ArrayList<>()));
    }
}
