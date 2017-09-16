package org.someth2say.taijitu.plugins;

import org.someth2say.taijitu.TaijituException;
import org.someth2say.taijitu.TaijituData;
import org.someth2say.taijitu.util.Named;

/**
 * @author Jordi Sola
 */
public interface TaijituPlugin extends Named {

    void preComparison(TaijituData taijituData) throws TaijituException;

    void postComparison(TaijituData taijituData) throws TaijituException;

    void start() throws TaijituException;

    void end() throws TaijituException;
}
