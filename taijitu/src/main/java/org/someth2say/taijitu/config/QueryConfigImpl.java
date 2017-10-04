package org.someth2say.taijitu.config;

import org.someth2say.taijitu.config.ConfigurationLabels.Comparison;

public class QueryConfigImpl implements QueryConfig {
    
    private final ComparisonConfigImpl comparisonConfig;
    private final String name;

    public QueryConfigImpl(final ComparisonConfigImpl comparisonConfig, final String name){
        this.comparisonConfig = comparisonConfig;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getStatement() {
        comparisonConfig.getConfig().getHierarchycalProperty(Comparison.QUERY, comparisonConfig.getRoot(), Comparison.SOURCE...Coparison.TARGET);
    }

    @Override
    public int getFetchSize() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getParameter(String parameterName) {
        // TODO Auto-generated method stub
        return null;
    }
}
