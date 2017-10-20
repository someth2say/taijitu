package org.someth2say.taijitu.config.impl;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.*;
import org.someth2say.taijitu.config.impl.apache.ApacheBasedComparisonConfig;


public class ComparisonConfigImpl extends NamedConfig implements ApacheBasedComparisonConfig {
    private final ImmutableHierarchicalConfiguration configuration;
    private final ComparisonConfig parent;

    //TODO: Sanity checks
    public ComparisonConfigImpl(final ImmutableHierarchicalConfiguration configuration, final ComparisonConfig parent) {
        super(configuration.getRootElementName());
        this.configuration = configuration;
        this.parent = parent;
    }


    /*** STRATEGY ***/
    private StrategyConfig strategyConfig = null;

    @Override
    public StrategyConfig getStrategyConfig() {
        if (strategyConfig == null) {
            strategyConfig = ApacheBasedComparisonConfig.super.getStrategyConfig();
        }
        return strategyConfig;
    }

    /**
     * QUERIES
     **/
    private QueryConfig sourceQuery = null;
    @Override
	public QueryConfig getSourceQueryConfig() {
        if (sourceQuery == null) {
            sourceQuery = ApacheBasedComparisonConfig.super.getSourceQueryConfig();
        }
        return sourceQuery;
    }


    private QueryConfig targetQuery = null;

    @Override
	public QueryConfig getTargetQueryConfig() {
        if (targetQuery == null) {
            targetQuery = ApacheBasedComparisonConfig.super.getTargetQueryConfig();
        }
        return targetQuery;
    }

    @Override
	public ImmutableHierarchicalConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public ComparisonConfig getParent() {
        return parent;
    }


}
