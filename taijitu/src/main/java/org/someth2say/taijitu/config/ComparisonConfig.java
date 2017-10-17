package org.someth2say.taijitu.config;

import org.someth2say.taijitu.util.Named;

public interface ComparisonConfig extends Named, QueryConfig {
    StrategyConfig getStrategyConfig();

    ComparisonPluginConfig[] getComparisonPluginConfigs();

    int getFetchSize();

    String[] getKeyFields();

    QueryConfig getSourceQueryConfig();

    QueryConfig getTargetQueryConfig();

    String getColumnMatchingStrategyName();

    String getStatement();

    Object[] getQueryParameters();

    String getDatabaseRef();
}
