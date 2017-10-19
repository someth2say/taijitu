package org.someth2say.taijitu.config.impl;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.ComparisonConfig;
import org.someth2say.taijitu.config.ConfigurationLabels.Comparison;
import org.someth2say.taijitu.config.ConfigurationLabels.Setup;
import org.someth2say.taijitu.config.NamedConfig;
import org.someth2say.taijitu.config.PluginConfig;
import org.someth2say.taijitu.config.QueryConfig;
import org.someth2say.taijitu.config.StrategyConfig;


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



    /*** PLUGINS ***/

    @Override
    public PluginConfig[] getComparisonPluginConfigs() {
        // right now, we only allow global plugins. Maybe we can set-up per-comparison plugins someday...
        return getParent().getComparisonPluginConfigs();
    }

    /**
     * DATABASE
     **/
    @Override
    public String getDatabaseRef() {
        String databaseRef = getConfiguration().getString(Comparison.DATABASE_REF);
        if (databaseRef == null) databaseRef = getParent().getDatabaseRef();
        return databaseRef;
    }

    /**
     * PROPERTIES
     **/
    @Override
    public int getFetchSize() {
        Integer fs = getConfiguration().getInteger(Setup.FETCH_SIZE, null);
        if (fs == null) {
            fs = getParent().getFetchSize();
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
        String[] keys = getConfiguration().get(String[].class, Comparison.Fields.KEY, null);
        return keys != null ? keys : getParent().getKeyFields();
    }

    @Override
    public String getColumnMatchingStrategyName() {
        String columnMatchingStrategy = getConfiguration().getString(Setup.COLUMN_MATCHING_STRATEGY);
        return columnMatchingStrategy != null ? columnMatchingStrategy : getParent().getColumnMatchingStrategyName();
    }

    @Override
    public String getStatement() {
        final String statement = getConfiguration().getString(Comparison.STATEMENT);
        return statement != null ? statement : getParent().getStatement();
    }

    @Override
    public Object[] getQueryParameters() {
        Object[] params = getConfiguration().get(Object[].class, Comparison.QUERY_PARAMETERS, null);
        if (params == null) {
            params = getParent().getQueryParameters();
        }
        return params;
    }

    
    /**
     * QUERIES
     **/
    private QueryConfig sourceQuery = null;

    public QueryConfig getSourceQueryConfig() {
        if (sourceQuery == null) {
            try {
                final ImmutableHierarchicalConfiguration sourceConfig = this.getConfiguration().immutableConfigurationAt(Comparison.SOURCE);
                sourceQuery = new QueryConfigImpl(sourceConfig, this);
            } catch (IllegalArgumentException e) {
                sourceQuery = getParent().getSourceQueryConfig();
            }
        }
        return sourceQuery;
    }

    private QueryConfig targetQuery = null;

    public QueryConfig getTargetQueryConfig() {
        if (targetQuery == null) {
            try {
                final ImmutableHierarchicalConfiguration targetConfig = this.getConfiguration().immutableConfigurationAt(Comparison.TARGET);
                targetQuery = new QueryConfigImpl(targetConfig, this);
            } catch (IllegalArgumentException e) {
                targetQuery = getParent().getSourceQueryConfig();
            }
        }
        return targetQuery;
    }

	public ImmutableHierarchicalConfiguration getConfiguration() {
		return configuration;
	}
	
	@Override
	public ComparisonConfig getParent() {
		return parent;
	}


}
