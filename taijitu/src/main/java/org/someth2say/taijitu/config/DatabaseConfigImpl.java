package org.someth2say.taijitu.config;

public class DatabaseConfigImpl implements DatabaseConfig {
    
    private String databaseName;

    public DatabaseConfigImpl(final TaijituConfig taijituConfig, final String databaseName){
        this.databaseName = databaseName;
    }

}
