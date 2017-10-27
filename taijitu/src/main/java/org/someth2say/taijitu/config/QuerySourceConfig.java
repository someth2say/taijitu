package org.someth2say.taijitu.config;

// TODO: QuerySourceConfig is just a subtype of SourceConfig. Should find a way to discriminate
public interface QuerySourceConfig extends SourceConfig {

    String getStatement();
    
    int getFetchSize();
    
    Object[] getQueryParameters();

    DatabaseConfig getDatabaseConfig();
}
