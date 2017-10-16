package org.someth2say.taijitu.config;

public interface TaijituConfig {
	ComparisonConfig[] getComparisons();

	DatabaseConfig[] getAllDatabaseConfigs();

	DatabaseConfig getDatabaseConfig(final String databaseName);

	int getThreads();

	int getFetchSize();

	String getConsoleLog();

	String getFileLog();

	String getOutputFolder();

	double getPrecisionThreshold();

	Boolean isUseScanClassPath();

	ComparisonPluginConfig[] getAllPluginsConfig();
	
	Object[] getQueryParameters();


}
