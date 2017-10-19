package org.someth2say.taijitu.config;

import org.someth2say.taijitu.util.Named;

import java.util.List;

public interface QueryConfig extends Named{
    
    String getName();
    
    String getStatement();
    
    int getFetchSize();
    
    String[] getKeyFields();

    String getDatabaseRef();

    Object[] getQueryParameters();

    List<EqualityConfig> getEqualityConfigs();
}
