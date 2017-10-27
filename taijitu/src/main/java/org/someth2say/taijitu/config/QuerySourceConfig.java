package org.someth2say.taijitu.config;

// TODO: QuerySourceConfigNode is just a subtype of SourceConfigNode. Should find a way to discriminate
public interface QuerySourceConfig extends SourceConfig {

    String getStatement();
    
    int getFetchSize();
    
    Object[] getQueryParameters();

    DatabaseConfig getDatabaseConfig();
}
