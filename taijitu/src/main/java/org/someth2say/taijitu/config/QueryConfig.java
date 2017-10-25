package org.someth2say.taijitu.config;

import org.someth2say.taijitu.util.Named;

import java.util.List;

public interface QueryConfig extends Named{

    String getStatement();
    
    int getFetchSize();
    
    List<String> getKeyFields();

    String getDatabaseRef();

    Object[] getQueryParameters();

    DatabaseConfig getDatabaseConfig();
}
