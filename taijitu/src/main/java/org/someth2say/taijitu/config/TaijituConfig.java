package org.someth2say.taijitu.config;

public interface TaijituConfig extends ComparisonConfig {
	ComparisonConfig[] getComparisons();

	DatabaseConfig[] getAllDatabaseConfigs();

	int getThreads();

	String getConsoleLog();

	String getFileLog();

	String getOutputFolder();

	Boolean isUseScanClassPath();

	PluginConfig[] getComparisonPluginConfigs();

}
