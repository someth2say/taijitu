package org.someth2say.taijitu.plugins;

import org.someth2say.taijitu.ComparisonRuntime;
import org.someth2say.taijitu.TaijituException;
import org.someth2say.taijitu.config.ComparisonPluginConfig;
import org.someth2say.taijitu.util.Named;

/**
 * @author Jordi Sola
 */
public interface TaijituPlugin extends Named {

    void preComparison(final ComparisonRuntime taijituData, final ComparisonPluginConfig comparisonConfig) throws TaijituException;

    void postComparison(final ComparisonRuntime taijituData, final ComparisonPluginConfig comparisonConfig) throws TaijituException;

    void start(final ComparisonPluginConfig config) throws TaijituException;

    void end(final ComparisonPluginConfig config) throws TaijituException;
}
