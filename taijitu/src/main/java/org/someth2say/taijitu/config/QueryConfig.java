package org.someth2say.taijitu.config;

public interface QueryConfig {
    
    public String getName();
    
    public String getStatement();
    
    public int getFetchSize();
    
    public String getParameter(final String parameterName);
}
