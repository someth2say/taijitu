package org.someth2say.taijitu.config.impl;

import org.apache.commons.configuration2.ConfigurationUtils;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.builder.fluent.PropertiesBuilderParameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.someth2say.taijitu.TaijituException;
import org.someth2say.taijitu.config.*;
import org.someth2say.taijitu.config.apache.ApacheTaijituConfigNode;
import org.someth2say.taijitu.config.node.TaijituConfigNode;
import org.someth2say.taijitu.source.CSVFileSource;
import org.someth2say.taijitu.source.ResultSetSource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.someth2say.taijitu.config.DefaultConfig.*;

public final class TaijituConfigImpl extends ComparisonConfigImpl implements TaijituConfig {

    @Deprecated
    private final ImmutableHierarchicalConfiguration configuration;
    private final TaijituConfig delegate;

    private TaijituConfigImpl(final ImmutableHierarchicalConfiguration configuration, TaijituConfig delegate) {
        super(configuration,null);
        this.configuration = configuration;
        this.delegate = delegate;
    }

    public static TaijituConfig fromFile(final String file) throws TaijituException {
        ImmutableHierarchicalConfiguration config = load(file);
        ApacheTaijituConfigNode atcn = new ApacheTaijituConfigNode(config);
        return new TaijituConfigImpl(config, atcn);
    }


    public static TaijituConfig fromProperties(final ImmutableHierarchicalConfiguration config) throws TaijituException {
        ApacheTaijituConfigNode atcn = new ApacheTaijituConfigNode(config);
        return new TaijituConfigImpl(config, atcn);
    }

    public ComparisonConfig getParent() {
        return null;
    }

    /**** UTILITIES *********************************************************************/

    /**
     * Tries to load configuration from supported format files (just properties right now...).
     *
     * @param configFile ConfigurationLabels file to load
     */
    private static ImmutableHierarchicalConfiguration load(final String configFile) throws TaijituException {
        final PropertiesBuilderParameters builderParameters = new Parameters().properties().setFileName(configFile);
        FileBasedConfigurationBuilder<PropertiesConfiguration> builder = new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class)
                .configure(builderParameters);
        return buildConfiguration(configFile, builder);
    }

    private static ImmutableHierarchicalConfiguration buildConfiguration(String configFile, FileBasedConfigurationBuilder<PropertiesConfiguration> builder) throws TaijituException {
        try {
            final PropertiesConfiguration pConfig = builder.getConfiguration();
            final HierarchicalConfiguration<?> hConfig = ConfigurationUtils.convertToHierarchical(pConfig);
            return ConfigurationUtils.unmodifiableConfiguration(hConfig);
        } catch (ConfigurationException e) {
            // loading of the configuration file failed
            throw new TaijituException("Unable to load properties from " + configFile, e);
        }
    }

    @Deprecated
    public ImmutableHierarchicalConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public ComparisonConfig[] getComparisons() {
        return getDelegate().getComparisons();
    }

    @Override
    public int getThreads() {
        return getDelegate().getThreads();
    }

    @Override
    public String getConsoleLog() {
        return getDelegate().getConsoleLog();
    }

    @Override
       public String getFileLog() {
        return getDelegate().getFileLog();
    }

    @Override
    public String getOutputFolder() {
        return getDelegate().getOutputFolder();
    }

    @Override
    public Boolean isUseScanClassPath() {
        return getDelegate().isUseScanClassPath();
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
            parentEqualityConfigs = List.of(DEFAULT_EQUALITY_CONFIG);
        }
        return parentEqualityConfigs;
    }

    @Override
    public StrategyConfig getStrategyConfig() {
        try {
            return getDelegatedStrategyConfig();
        } catch (IllegalArgumentException | ConfigurationRuntimeException e) {
            //No Strategy defined (or many)
            return getParent() != null ? getParent().getStrategyConfig() : DEFAULT_STRATEGY_CONFIG;
        }
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

    @Override
    public List<SourceConfig> getSourceConfigs() {
        List<SourceConfig> localSourceConfigs = getDelegatedSourceConfigs();
        if (getParent() != null) localSourceConfigs.addAll(getParent().getSourceConfigs());
        return localSourceConfigs;
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

    public TaijituConfigNode getDelegate() {
        return delegate;
    }
}
