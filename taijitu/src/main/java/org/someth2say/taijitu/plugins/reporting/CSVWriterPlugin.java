package org.someth2say.taijitu.plugins.reporting;

import org.someth2say.taijitu.config.interfaces.IComparisonCfg;
import org.someth2say.taijitu.config.interfaces.IPluginCfg;

/**
 * @author Jordi Sola
 */
public class CSVWriterPlugin extends AbstractWriterPlugin {

    @Override
    public String getName() {
        return "csv";
    }

    @Override
    public void preComparison(IPluginCfg comparisonConfig, IComparisonCfg comparisonCfg) {}

    @Override
    public void postComparison(IPluginCfg pluginCfg, IComparisonCfg comparisonCfg) {}
}