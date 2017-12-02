package org.someth2say.taijitu.cli.plugins;

import org.someth2say.taijitu.cli.config.interfaces.IComparisonCfg;
import org.someth2say.taijitu.cli.config.interfaces.IPluginCfg;
import org.someth2say.taijitu.cli.util.Named;

/**
 * @author Jordi Sola
 */
public interface TaijituPlugin extends Named {

    void preComparison(final IPluginCfg pluginCfg, final IComparisonCfg comparisonCfg);

    void postComparison(final IPluginCfg pluginCfg, final IComparisonCfg comparisonCfg);

    void start(final IPluginCfg config);

    void end(final IPluginCfg config);
}
