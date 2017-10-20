package org.someth2say.taijitu.config;

import org.someth2say.taijitu.util.Named;

public interface QueryConfig extends Named{

    String getStatement();
    
    int getFetchSize();
    
    String[] getKeyFields();

    String getDatabaseRef();

    Object[] getQueryParameters();

}
