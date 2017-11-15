package org.someth2say.taijitu.plugins.reporting;

import org.someth2say.taijitu.config.interfaces.IComparisonCfg;
import org.someth2say.taijitu.config.interfaces.IPluginCfg;

/**
 * @author Jordi Sola
 */
public class XLSWriterPlugin extends AbstractWriterPlugin {

    @Override
    public String getName() {
        return "xls";
    }

    @Override
    public void preComparison(IPluginCfg pluginCfg, IComparisonCfg comparisonCfg) {

    }

    @Override
    public void postComparison(IPluginCfg pluginCfg, IComparisonCfg comparisonCfg) {

    }
}