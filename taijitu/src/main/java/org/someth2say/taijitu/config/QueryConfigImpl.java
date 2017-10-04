package org.someth2say.taijitu.config;

public class QueryConfigImpl implements QueryConfig {
    
    private final ComparisonConfig comparisonConfig;
    private final String id;

    public QueryConfigImpl(final ComparisonConfig comparisonConfig, final String id){
        this.comparisonConfig = comparisonConfig;
        this.id = id;
    }
}
