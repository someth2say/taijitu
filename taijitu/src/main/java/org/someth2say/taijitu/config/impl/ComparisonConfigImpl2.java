package org.someth2say.taijitu.config.impl;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.*;
import org.someth2say.taijitu.config.ConfigurationLabels.Comparison;
import org.someth2say.taijitu.config.ConfigurationLabels.Setup;


public class ComparisonConfigImpl2 extends NamedConfig implements ComparisonConfig {
    private final ImmutableHierarchicalConfiguration configuration;
    private final ComparisonConfig parent;

    //TODO: Sanity checks
    public ComparisonConfigImpl2(final ImmutableHierarchicalConfiguration configuration, final ComparisonConfig parent) {
        super(configuration.getRootElementName());
        this.configuration = configuration.immutableConfigurationAt(getName());
        this.parent = parent;
    }


    /*** STRATEGY ***/
    private StrategyConfig strategyConfig = null;

    @Override
    public StrategyConfig getStrategyConfig() {
        if (strategyConfig == null) {
            try {
                ImmutableHierarchicalConfiguration strategyConfiguration = configuration.immutableConfigurationAt(Comparison.STRATEGY);
                strategyConfig = new StrategyConfigImpl2(strategyConfiguration);
            } catch (IllegalArgumentException e) {
                //No Strategy defined (or many)
                strategyConfig = parent.getStrategyConfig();
            }
        }
        return strategyConfig;
    }

    /*** PLUGINS ***/

    @Override
    public ComparisonPluginConfig[] getComparisonPluginConfigs() {
        // right now, we only allow global plugins. Maybe we can set-up per-comparison plugins someday...
        return parent.getComparisonPluginConfigs();
    }

    /**
     * DATABASE
     **/
    @Override
    public String getDatabaseRef() {
        String databaseRef = configuration.getString(Comparison.DATABASE_REF);
        if (databaseRef == null) databaseRef = parent.getDatabaseRef();
        return databaseRef;
    }

    /**
     * PROPERTIES
     **/
    @Override
    public int getFetchSize() {
        Integer fs = configuration.getInteger(Setup.FETCH_SIZE, null);
        if (fs == null) {
            fs = parent.getFetchSize();
        }
        return fs;
    }

    /**
     * Key fields are fields that define both entries are the same.
     * Some strategies may not need this field (i.e. strategies that assume all
     * entries are returned by both queries, and just need to check the contents)
     */
    //TODO: Consider moving form Arrays to Lists
    @Override
    public String[] getKeyFields() {
        String[] keys = configuration.get(String[].class, Comparison.Fields.KEY, null);
        return keys != null ? keys : parent.getKeyFields();
    }

    @Override
    public String getColumnMatchingStrategyName() {
        String columnMatchingStrategy = configuration.getString(Setup.COLUMN_MATCHING_STRATEGY);
        return columnMatchingStrategy != null ? columnMatchingStrategy : parent.getColumnMatchingStrategyName();
    }

    @Override
    public String getStatement() {
        final String statement = configuration.getString(Comparison.STATEMENT);
        return statement != null ? statement : parent.getStatement();
    }

    @Override
    public Object[] getQueryParameters() {
        Object[] keys = configuration.get(Object[].class, Comparison.QUERY_PARAMETERS, null);
        if (keys == null) {
            keys = parent.getQueryParameters();
        }
        return keys;
    }

    /**
     * QUERIES
     **/
    private QueryConfig sourceQuery = null;

    public QueryConfig getSourceQueryConfig() {
        if (sourceQuery == null) {
            try {
                final ImmutableHierarchicalConfiguration sourceConfig = this.configuration.immutableConfigurationAt(Comparison.SOURCE);
                sourceQuery = new QueryConfigImpl2(sourceConfig, this);
            } catch (IllegalArgumentException e) {
                sourceQuery = parent.getSourceQueryConfig();
            }
        }
        return sourceQuery;
    }

    private QueryConfig targetQuery = null;

    public QueryConfig getTargetQueryConfig() {
        if (targetQuery == null) {
            try {
                final ImmutableHierarchicalConfiguration targetConfig = this.configuration.immutableConfigurationAt(Comparison.TARGET);
                targetQuery = new QueryConfigImpl2(targetConfig, this);
            } catch (IllegalArgumentException e) {
                targetQuery = parent.getSourceQueryConfig();
            }
        }
        return targetQuery;
    }


}
