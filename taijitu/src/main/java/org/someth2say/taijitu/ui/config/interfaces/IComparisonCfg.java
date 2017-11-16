package org.someth2say.taijitu.ui.config.interfaces;

import org.someth2say.taijitu.util.Named;

import java.util.List;

public interface IComparisonCfg extends ISourceCfg, IStrategyCfg, IEqualityCfg, IPluginCfg, Named {

    IStrategyCfg getStrategyConfig();

    List<IEqualityCfg> getEqualityConfigs();

    List<ISourceCfg> getSourceConfigs();

    List<IPluginCfg> getPluginConfigs();

}
