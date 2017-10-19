package org.someth2say.taijitu.config;

public interface TaijituConfig {
	ComparisonConfig[] getComparisons();

	DatabaseConfig[] getAllDatabaseConfigs();

	int getThreads();

	int getFetchSize();

	String getConsoleLog();

	String getFileLog();

	String getOutputFolder();

	Boolean isUseScanClassPath();

	PluginConfig[] getComparisonPluginConfigs();
	
	Object[] getQueryParameters();

	StrategyConfig getStrategyConfig();

    String getDatabaseRef();

	String[] getKeyFields();

	String getColumnMatchingStrategyName();

	String getStatement();

	QueryConfig getSourceQueryConfig();
}
