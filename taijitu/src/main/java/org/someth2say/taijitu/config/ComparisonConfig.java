package org.someth2say.taijitu.config;

import java.util.List;

import org.someth2say.taijitu.util.Named;

public interface ComparisonConfig extends Named, QueryConfig {
    StrategyConfig getStrategyConfig();

    PluginConfig[] getComparisonPluginConfigs();

    int getFetchSize();

    String[] getKeyFields();

    QueryConfig getSourceQueryConfig();

    QueryConfig getTargetQueryConfig();

    String getColumnMatchingStrategyName();

    String getStatement();

    Object[] getQueryParameters();

    String getDatabaseRef();

	List<EqualityConfig> getEqualityConfigs();
}
