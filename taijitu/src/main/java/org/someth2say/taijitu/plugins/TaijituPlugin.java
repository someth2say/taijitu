package org.someth2say.taijitu.plugins;

import org.someth2say.taijitu.TaijituException;
import org.someth2say.taijitu.config.interfaces.IPluginCfg;
import org.someth2say.taijitu.util.Named;

/**
 * @author Jordi Sola
 */
public interface TaijituPlugin extends Named {

    void preComparison(final IPluginCfg comparisonConfig) throws TaijituException;

    void postComparison(final IPluginCfg comparisonConfig) throws TaijituException;

    void start(final IPluginCfg config) throws TaijituException;

    void end(final IPluginCfg config) throws TaijituException;
}
