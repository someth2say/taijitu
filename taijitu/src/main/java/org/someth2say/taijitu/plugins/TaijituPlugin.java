package org.someth2say.taijitu.plugins;

import org.someth2say.taijitu.ComparisonRuntime;
import org.someth2say.taijitu.TaijituException;
import org.someth2say.taijitu.config.PluginConfig;
import org.someth2say.taijitu.util.Named;

/**
 * @author Jordi Sola
 */
public interface TaijituPlugin extends Named {

    void preComparison(final ComparisonRuntime taijituData, final PluginConfig comparisonConfig) throws TaijituException;

    void postComparison(final ComparisonRuntime taijituData, final PluginConfig comparisonConfig) throws TaijituException;

    void start(final PluginConfig config) throws TaijituException;

    void end(final PluginConfig config) throws TaijituException;
}
