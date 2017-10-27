package org.someth2say.taijitu.config2;

import java.util.List;

public interface ComparisonConfig  {
    StrategyConfig getStrategyConfig();

    List<PluginConfig> getComparisonPluginConfigs();

    String getMatchingStrategyName();

    List<EqualityConfig> getEqualityConfigs();

    List<SourceConfig> getSourceConfigs();
}
