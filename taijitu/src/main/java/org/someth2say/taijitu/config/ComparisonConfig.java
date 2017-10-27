package org.someth2say.taijitu.config;

import java.util.List;
//TODO: Forcing here to LIST all kind of Source configs is awfull!!!
public interface ComparisonConfig extends QuerySourceConfig, FileSourceConfig {
    StrategyConfig getStrategyConfig();

    PluginConfig[] getComparisonPluginConfigs();

    String getMatchingStrategyName();

    List<EqualityConfig> getEqualityConfigs();

    List<SourceConfig> getSourceConfigs();
}
