package org.someth2say.taijitu.config;

public interface ComparisonConfig {

    public StrategyConfig getStrategyConfig();

    public ComparisonPluginConfig[] getComparisonPluginConfigs();

    public DatabaseConfig getDatabaseConfig();

    public int getFetchSize();

    public String[] getKeyFields();
    
    public String[] getCompareFields();
    
    public double getPrecisionThreshold();
    
    public QueryConfig getSourceQueryConfig();
    
    public QueryConfig getTargetQueryConfig();
}
