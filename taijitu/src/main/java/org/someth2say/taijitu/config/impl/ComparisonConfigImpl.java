package org.someth2say.taijitu.config.impl;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.*;
import org.someth2say.taijitu.config.impl.apache.ApacheBasedComparisonConfig;
import org.someth2say.taijitu.config.impl.apache.ApacheBasedQuerySourceConfig;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;


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

    /*** SOURCES ***/
    private List<SourceConfig> sourceConfigCache = null;
    @Override
    public List<SourceConfig> getSourceConfigs() {
        if (sourceConfigCache == null) {
            sourceConfigCache = ApacheBasedComparisonConfig.super.getSourceConfigs();
        }
        return sourceConfigCache;
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
