package org.someth2say.taijitu.config.impl;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.someth2say.taijitu.config.*;
import org.someth2say.taijitu.source.CSVFileSource;
import org.someth2say.taijitu.source.ResultSetSource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.someth2say.taijitu.config.DefaultConfig.*;


public class ComparisonConfigImpl extends NamedConfig implements ComparisonConfig, FileSourceConfig, QuerySourceConfig, SourceConfig {
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

    //@Override
    public StrategyConfig getStrategyConfig() {
        if (strategyConfig == null) {
            StrategyConfig result;
            try {
                result = getDelegatedStrategyConfig();
            } catch (IllegalArgumentException | ConfigurationRuntimeException e) {
                //No Strategy defined (or many)
                result = getParent() != null ? getParent().getStrategyConfig() : DEFAULT_STRATEGY_CONFIG;
            }
            strategyConfig = result;
        }
        return strategyConfig;
    }

    /*** SOURCES ***/
    private List<SourceConfig> sourceConfigCache = null;
    @Override
    public List<SourceConfig> getSourceConfigs() {
        if (sourceConfigCache == null) {
            List<SourceConfig> localSourceConfigs = getDelegatedSourceConfigs();
            if (getParent() != null) localSourceConfigs.addAll(getParent().getSourceConfigs());
            sourceConfigCache = localSourceConfigs;
        }
        return sourceConfigCache;
    }

    public ImmutableHierarchicalConfiguration getConfiguration() {
        return configuration;
    }

    public ComparisonConfig getParent() {
        return parent;
    }

    @Override
    public PluginConfig[] getComparisonPluginConfigs() {
        // right now, we only allow global plugins. Maybe we can allow per-comparison plugins someday...
        return getParent() != null ? getParent().getComparisonPluginConfigs() : DEFAULT_PLUGINS_CONFIG;
    }

    @Override
    public List<EqualityConfig> getEqualityConfigs() {
        final List<EqualityConfig> equalityConfigs = getDelegatedEqualityConfigs();
        final List<EqualityConfig> parentEqualityConfigs = getParentEqualityConfigs();
        equalityConfigs.addAll(parentEqualityConfigs);
        return equalityConfigs;
    }

    public List<EqualityConfig> getDelegatedEqualityConfigs() {
        List<ImmutableHierarchicalConfiguration> thisEqConfigs;
        try {
            thisEqConfigs = getConfiguration().immutableChildConfigurationsAt(ConfigurationLabels.Comparison.EQUALITY);
        } catch (ConfigurationRuntimeException e) {
            thisEqConfigs = Collections.emptyList();
        }
        return thisEqConfigs.stream().map(EqualityConfigImpl::new).collect(Collectors.toList());
    }

    public List<EqualityConfig> getParentEqualityConfigs() {
        List<EqualityConfig> parentEqualityConfigs;
        if (getParent() != null) {
            parentEqualityConfigs = getParent().getEqualityConfigs();
        } else {
            parentEqualityConfigs= List.of(DEFAULT_EQUALITY_CONFIG);
        }
        return parentEqualityConfigs;
    }

    public StrategyConfig getDelegatedStrategyConfig() {
        ImmutableHierarchicalConfiguration strategyConfiguration = getConfiguration().immutableConfigurationAt(ConfigurationLabels.Comparison.STRATEGY);
        return new StrategyConfigImpl(strategyConfiguration);
    }

    @Override
    public String getMatchingStrategyName() {
        String matchingStrategy = getDelegatedString();
        return matchingStrategy != null ? matchingStrategy
                : getParent() != null ? getParent().getMatchingStrategyName() : DEFAULT_MATCHING_STRATEGY_NAME;
    }

    public String getDelegatedString() {
        return getConfiguration().getString(ConfigurationLabels.Setup.MATCHING_STRATEGY);
    }

    public List<SourceConfig> getDelegatedSourceConfigs() {
        final List<ImmutableHierarchicalConfiguration> sourceConfigs = this.getConfiguration().immutableChildConfigurationsAt(ConfigurationLabels.Comparison.SOURCES);
        return sourceConfigs.stream().map(this::buildSpecificConfig).collect(Collectors.toList());
    }

    public SourceConfig buildSpecificConfig(final ImmutableHierarchicalConfiguration config) {
        //TODO: May we have some kind of configuration registry (but just for this apache implementation)?
        String sourceType = config.getString(ConfigurationLabels.Comparison.SOURCE_TYPE);
        switch (sourceType) {
            case ResultSetSource.NAME:
                return new QuerySourceConfigImpl(config, this);
            case CSVFileSource.NAME:
                return new FileSourceConfigImpl(config, this);
            default:
                return null;
        }
    }

    @Override
    public String getPath() {
        String statement = getConfiguration().getString(ConfigurationLabels.Comparison.FILE_PATH);
        return statement != null ? statement : getParent().getPath();
    }

    @Override
    public String getStatement() {
        String statement = getConfiguration().getString(ConfigurationLabels.Comparison.STATEMENT);
        return statement != null ? statement : getParent() != null ? getParent().getStatement() : null;
    }

    @Override
    public int getFetchSize() {
        Integer fs = getConfiguration().getInteger(ConfigurationLabels.Setup.FETCH_SIZE, null);
        return fs != null ? fs : getParent() != null ? getParent().getFetchSize() : DEFAULT_FETCHSIZE;
    }

    @Override
    public Object[] getQueryParameters() {
        final Object[] params = getConfiguration().get(Object[].class, ConfigurationLabels.Comparison.QUERY_PARAMETERS, null);
        return params != null ? params : getParent() != null ? getParent().getQueryParameters() : DEFAULT_QUERY_PARAMETERS;
    }

    @Override
    public DatabaseConfig getDatabaseConfig() {
        try {
            final ImmutableHierarchicalConfiguration databaseConfig = getConfiguration().immutableConfigurationAt(ConfigurationLabels.Sections.DATABASE);
            return new DatabaseConfigImpl(databaseConfig);
        } catch (IllegalArgumentException | ConfigurationRuntimeException e) {
            return getParent() != null ? getParent().getDatabaseConfig() : null;
        }
    }

    @Override
    public List<String> getKeyFields() {
        List<String> keys = getConfiguration().getList(String.class, ConfigurationLabels.Comparison.Fields.KEYS, null);
        return keys != null ? keys : getParent() != null ? getParent().getKeyFields() : null;
    }

    @Override
    public String getType() {
        String statement = getConfiguration().getString(ConfigurationLabels.Comparison.SOURCE_TYPE);
        return statement != null ? statement : getParent() != null ? getParent().getType() : null;
    }
}
