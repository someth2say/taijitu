package org.someth2say.taijitu.config;

import org.someth2say.taijitu.commons.StringUtil;
import org.someth2say.taijitu.config.ConfigurationLabels.Comparison;
import org.someth2say.taijitu.query.properties.HProperties;

import java.util.Arrays;

public class QueryConfigImpl implements QueryConfig {

    private final ComparisonConfigImpl comparisonConfig;
    private final String name;

    public QueryConfigImpl(final ComparisonConfigImpl comparisonConfig, final String name) {
        this.comparisonConfig = comparisonConfig;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getStatement() {
        return getProperty(Comparison.QUERY, null);
    }

    private String getProperty(final String property, final String defaultValue) {
        //TODO: Is this too verbose? comparison.comparisonName.query.queryname.property.XXX=YYY
        String[] queryRoot = getQueryPropertiesRoot();

        HProperties properties = comparisonConfig.getConfig();

        String hierarchicalProperty = properties.getHierarchycalProperty(property, null, queryRoot);
        return hierarchicalProperty != null ? hierarchicalProperty : defaultValue;
    }

    private String[] getQueryPropertiesRoot() {
        final String[] configRoot = comparisonConfig.getPropertiesRoot();
        String[] queryRoot = Arrays.copyOf(configRoot, configRoot.length + 3);
        queryRoot[configRoot.length - 1] = Comparison.QUERY;
        queryRoot[configRoot.length] = getName();
        return queryRoot;
    }

    @Override
    public int getFetchSize() {
        String property = getProperty(ConfigurationLabels.Setup.FETCH_SIZE, null);
        if (property == null) return comparisonConfig.getFetchSize();
        try {
            return Integer.parseInt(property);
        } catch (NumberFormatException e) {
            return DefaultConfig.DEFAULT_FETCHSIZE;
        }
    }

    @Override
    public String[] getKeyFields() {
        String property = getProperty(Comparison.Fields.KEY, null);
        //TODO: Memoize
        return property != null ? StringUtil.splitAndTrim(property) : comparisonConfig.getKeyFields();
    }

    @Override
    public String getDatabase() {
        String property = getProperty(Comparison.DATABASE_REF, null);
        return property != null ? property : comparisonConfig.getDatabase();
    }

    @Override
    public Object[] getQueryParameters() {
        String property = getProperty(Comparison.QUERY_PARAMETERS, null);
        return property != null ?StringUtil.splitAndTrim(property):comparisonConfig.getQueryParameters();
    }
}
