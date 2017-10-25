package org.someth2say.taijitu.plugins;

import org.someth2say.taijitu.ComparisonContext;
import org.someth2say.taijitu.TaijituException;
import org.someth2say.taijitu.config.PluginConfig;
import org.someth2say.taijitu.util.Named;

/**
 * @author Jordi Sola
 */
public interface TaijituPlugin extends Named {

    void preComparison(final ComparisonContext taijituData, final PluginConfig comparisonConfig) throws TaijituException;

    void postComparison(final ComparisonContext taijituData, final PluginConfig comparisonConfig) throws TaijituException;

    void start(final PluginConfig config) throws TaijituException;

    void end(final PluginConfig config) throws TaijituException;
}
