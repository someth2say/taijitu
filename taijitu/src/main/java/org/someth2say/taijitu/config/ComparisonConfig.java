package org.someth2say.taijitu.config;

import java.util.List;

import org.someth2say.taijitu.util.Named;

public interface ComparisonConfig extends Named, QueryConfig {
    StrategyConfig getStrategyConfig();

    PluginConfig[] getComparisonPluginConfigs();

    QueryConfig getSourceQueryConfig();

    QueryConfig getTargetQueryConfig();

    String getColumnMatchingStrategyName();

    List<EqualityConfig> getEqualityConfigs();
}
