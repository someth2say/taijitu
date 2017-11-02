package org.someth2say.taijitu.config.interfaces;

import org.someth2say.taijitu.util.Named;

import java.util.List;

public interface IComparisonCfg extends ISourceCfg, IStrategyCfg, IEqualityCfg, IPluginCfg, Named {

    IStrategyCfg getStrategyConfig();

    String getMatchingStrategyName();

    List<IEqualityCfg> getEqualityConfigs();

    List<ISourceCfg> getSourceConfigs();

    List<IPluginCfg> getComparisonPluginConfigs();

}
