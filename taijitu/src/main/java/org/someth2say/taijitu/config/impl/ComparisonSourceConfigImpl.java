package org.someth2say.taijitu.config.impl;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.*;
import org.someth2say.taijitu.config.impl.apache.ApacheBasedComparisonSourceConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ComparisonSourceConfigImpl extends NamedConfig implements ApacheBasedComparisonSourceConfig {
    private final ImmutableHierarchicalConfiguration configuration;
    private final ComparisonConfig parent;

    //TODO: Sanity checks
    public ComparisonSourceConfigImpl(final ImmutableHierarchicalConfiguration configuration, final ComparisonConfig parent) {
        super(configuration.getRootElementName());
        this.configuration = configuration;
        this.parent = parent;
    }

    /*** STRATEGY ***/
    private StrategyConfig strategyConfig = null;

    @Override
    public StrategyConfig getStrategyConfig() {
        if (strategyConfig == null) {
            strategyConfig = ApacheBasedComparisonSourceConfig.super.getStrategyConfig();
        }
        return strategyConfig;
    }

    /**
     * QUERIES
     **/
    private Map<String, QuerySourceConfig> queryCache = new ConcurrentHashMap<>();

    @Override
    public QuerySourceConfig getSourceConfig(String sourceId) {
        return queryCache.computeIfAbsent(sourceId, ApacheBasedComparisonSourceConfig.super::getSourceConfig);
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
