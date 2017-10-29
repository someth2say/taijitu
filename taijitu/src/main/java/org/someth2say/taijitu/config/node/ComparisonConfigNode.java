package org.someth2say.taijitu.config.node;

import org.someth2say.taijitu.config.EqualityConfig;
import org.someth2say.taijitu.config.PluginConfig;
import org.someth2say.taijitu.config.SourceConfig;
import org.someth2say.taijitu.config.StrategyConfig;

import java.util.List;

public interface ComparisonConfigNode  {
    StrategyConfig getStrategyConfig();

    PluginConfig[] getComparisonPluginConfigs();

    String getMatchingStrategyName();

    List<EqualityConfig> getEqualityConfigs();

    List<SourceConfig> getSourceConfigs();

}
