package org.someth2say.taijitu.config;

import org.someth2say.taijitu.util.Named;

public interface QueryConfig extends Named{
    
    String getName();
    
    String getStatement();
    
    int getFetchSize();
    
//    String getParameter(final String parameterName);

    String[] getKeyFields();

    String getDatabaseRef();

    Object[] getQueryParameters();
}
