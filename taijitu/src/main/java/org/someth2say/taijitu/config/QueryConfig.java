package org.someth2say.taijitu.config;

public interface QueryConfig {
    
    String getName();
    
    String getStatement();
    
    int getFetchSize();
    
//    String getParameter(final String parameterName);

    String[] getKeyFields();

    String getDatabase();

    Object[] getQueryParameters();
}
