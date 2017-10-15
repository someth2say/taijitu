package org.someth2say.taijitu.config;

import java.util.Properties;

public interface ComparisonConfig {
	String getName();

	StrategyConfig getStrategyConfig();

	ComparisonPluginConfig[] getComparisonPluginConfigs();

	DatabaseConfig getDatabaseConfig();

	int getFetchSize();

	String[] getKeyFields();

	String[] getCompareFields();

	double getPrecisionThreshold();

	QueryConfig getSourceQueryConfig();

	QueryConfig getTargetQueryConfig();

	Properties getAllParameters();

    String getColumnMatcher();

	String getStatement();
	
	Object[] getQueryParameters();
}
