package org.someth2say.taijitu.config;

import org.someth2say.taijitu.commons.StringUtil;
import org.someth2say.taijitu.config.ConfigurationLabels.Comparison;

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
        return getProperty(getName(), null);
    }

    public String getProperty(final String property, final String defaultValue) {
        //TODO: Is this too verbose?
        //comparison.comparisonName.query.queryname.property.XXX=YYY
        final String[] configRoot = comparisonConfig.getPropertiesRoot();
        String[] queryRoot = Arrays.copyOf(configRoot, configRoot.length + 3);
        queryRoot[configRoot.length - 1] = Comparison.QUERY;
        queryRoot[configRoot.length] = getName();
        String hierarchycalProperty = comparisonConfig.getConfig().getHierarchycalProperty(property, null, queryRoot);
        return hierarchycalProperty != null ? hierarchycalProperty : defaultValue;
    }

    @Override
    public int getFetchSize() {
        String property = getProperty(ConfigurationLabels.Setup.FETCH_SIZE, null);
        try {
            return Integer.parseInt(property);
        } catch (NumberFormatException e) {
            return DefaultConfig.DEFAULT_FETCHSIZE;
        }
    }

    @Override
    // TODO: Parameters should be resolved at HProperties level (string replacement), not at config level.
    public String getParameter(String parameterName) {
        String propertyName = comparisonConfig.getConfig().joinSections(Comparison.PARAMETERS, parameterName);
        String propertyValue = getProperty(propertyName, null);
        return propertyValue != null ? propertyValue : comparisonConfig.getParameter(parameterName);
    }

    @Override
    public String[] getKeyFields() {
        String property = getProperty(Comparison.Fields.KEY, null);
        //TODO: Memoize
        return StringUtil.splitAndTrim(property);
    }
}
