package org.someth2say.taijitu.config2;

public interface QuerySourceConfig  {

    String getStatement();
    
    int getFetchSize();
    
    Object[] getQueryParameters();

    DatabaseConfig getDatabaseConfig();
}
