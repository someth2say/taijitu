package org.someth2say.taijitu.config;

public interface TaijituConfig {
    public ComparisonConfig[] getComparisons();

    public DatabaseConfig getDatabaseConfig(final String databaseName);

    public int getThreads();

    public int getFetchSize();

    public String getConsoleLog();

    public String getFileLog();

    public String getOutputFolder();

    public double getPrecisionThreshold();
    
    public  Boolean isUseScanClassPath();
    
}
