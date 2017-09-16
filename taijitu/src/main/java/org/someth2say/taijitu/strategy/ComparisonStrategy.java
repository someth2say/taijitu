package org.someth2say.taijitu.strategy;

import org.someth2say.taijitu.TaijituException;
import org.someth2say.taijitu.TaijituData;
import org.someth2say.taijitu.util.Named;

/**
 * Created by Jordi Sola on 16/02/2017.
 */
public interface ComparisonStrategy extends Named {

    void runComparison(final TaijituData taijituData) throws TaijituException;
}
