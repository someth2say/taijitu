package org.someth2say.taijitu.config;

import java.util.List;
// TODO: QuerySourceConfig is just a subtype of SourceConfig. Should find a way to discriminate
public interface QuerySourceConfig extends SourceConfig {

    String getStatement();
    
    int getFetchSize();
    
    List<String> getKeyFields();

    Object[] getQueryParameters();

    DatabaseConfig getDatabaseConfig();
}
