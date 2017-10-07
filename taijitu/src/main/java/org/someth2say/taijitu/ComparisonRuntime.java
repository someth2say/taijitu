package org.someth2say.taijitu;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.someth2say.taijitu.query.database.IConnectionFactory;
import org.someth2say.taijitu.query.Query;
import org.someth2say.taijitu.query.QueryUtilsException;
import org.someth2say.taijitu.query.columnDescription.ColumnDescriptionUtils;
import org.someth2say.taijitu.commons.StringUtil;
import org.someth2say.taijitu.compare.ComparisonResult;
import org.someth2say.taijitu.compare.PrecissionThresholdComparator;
import org.someth2say.taijitu.config.ComparisonConfig;
import org.someth2say.taijitu.config.ConfigurationLabels;
import org.someth2say.taijitu.config.TaijituConfigImpl;
import org.someth2say.taijitu.plugins.PluginRegistry;
import org.someth2say.taijitu.plugins.TaijituPlugin;
import org.someth2say.taijitu.strategy.ComparisonStrategy;
import org.someth2say.taijitu.strategy.ComparisonStrategyRegistry;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jordi Sola
 *         This class keep all values defined for a single comparison, as per in configuration file.
 */
public class ComparisonRuntime {
    private static final Logger logger = Logger.getLogger(ComparisonRuntime.class);
    private final Query source;
    private final Query target;
    private final String testName;
    private final Map<Class<?>, Comparator<Object>> comparators;
    private final ComparisonStrategy strategy;
    //private final ComparisonResult result;
    private String header;
    private String[] fields;
    private String[] keyFields;
    private String[] compareFields;
    private Double precisionThreshold;
    //private final ComparisonConfig comparisonConfig;

    //TODO: Rename to "ComparisonData"
    public ComparisonRuntime(final ComparisonConfig comparisonConfig) throws TaijituException {
        this.comparisonConfig = comparisonConfig;
        source = new Query(comparisonConfig.getSourceQueryConfig());
        target = new Query(comparisonConfig.getTargetQueryConfig());
        final boolean queryOptimization = TaijituConfigImpl.isQueryOptimization(_testName);

        //this.result = new ComparisonResult(comparisonConfig);

        this.comparators = buildComparators();

        this.strategy = buildStrategy(_testName);
    }

    private ComparisonStrategy buildStrategy(final String testName) throws TaijituException {
        String strategyName = TaijituConfigImpl.getStrategyName(testName);
        return ComparisonStrategyRegistry.getStrategy(strategyName);
    }


    private Map<Class<?>, Comparator<Object>> buildComparators() {
        Map<Class<?>, Comparator<Object>> res = new HashMap<>();
        final double threshold = getPrecisionThreshold();
        if (threshold > 0) {
            res.put(BigDecimal.class, new PrecissionThresholdComparator(threshold));
        }
        return res;
    }

    private Query buildQuery(String _testName, IConnectionFactory connectionFactory, String queryStr, String queryName, String discriminator) throws TaijituException, QueryUtilsException {

        String actualName = StringUtils.isEmpty(queryName) ? discriminator + "." + _testName : queryName;

        final List<Object> targetQueryParameterValues = prepareParameterValues(queryStr);
        String replacedQueryStr = replaceQueryParameterTags(queryStr);

        final String dataBaseName = TaijituConfigImpl.getDatabaseName(testName, discriminator);
        return new Query(actualName, replacedQueryStr, connectionFactory, dataBaseName, getFields(), targetQueryParameterValues);
    }


    /**
     * Replace parameters by '?' in order to be assigned in JDBC queries
     *
     * @param query String for the query
     * @return Same query string, but with all parameter tags replaces by ?
     */
    private String replaceQueryParameterTags(final String query) {
        final Pattern parameterPattern = Pattern.compile("\\{(.*?)\\}");
        final Matcher parameterMatcher = parameterPattern.matcher(query);
        return parameterMatcher.replaceAll("?");
    }

    /**
     * Generate the list of parameter values, in the same order as they appear on the query.
     * Parameter values are take from configuration properties.
     *
     * @param query The query string
     * @return The list of values to be used for query parameters
     */
    private List<Object> prepareParameterValues(final String query) throws TaijituException {
        final List<Object> values = new ArrayList<>();
        // 1) Store parameters in query order
        final Pattern parameterPattern = Pattern.compile("\\{(.*?)\\}");
        final Matcher parameterMatcher = parameterPattern.matcher(query);
        while (parameterMatcher.find()) {
            final String parameterTag = parameterMatcher.group();
            final String parameterName = parameterTag.substring(1, parameterTag.length() - 1);

            String parameterValue = TaijituConfigImpl.getProperty(testName, parameterName);

            if (parameterTag.contains(ConfigurationLabels.DATE_PARAMETER_KEYWORD)) {
                values.add(TaijituConfigImpl.parseDate(parameterName, parameterValue));
            } else {
                values.add(parameterValue);
            }
        }

        return values;
    }

    /**
     * @return the header
     */
    private String getHeader() {
        if (header == null) {
            header = TaijituConfigImpl.getHeader(this.testName);
        }
        return header;
    }

    public String[] getFields() {
        if (fields == null) {
            final String headerStr = getHeader();
            if (headerStr != null) {
                fields = StringUtil.splitAndTrim(headerStr);
            }
        }
        return fields != null ? fields : null;
    }

    public void setFields(final String[] _fields) {
        this.fields = _fields;
        //as key fields and compare fields depend on fields, those should be updated
        this.keyFields = null;
        this.compareFields = null;
    }

    /**
     * @return the keyFields
     */
    public String[] getKeyFields() {
        if (keyFields == null) {
            String keyHeader = TaijituConfigImpl.getKeyFields(testName);
            if (keyHeader == null) {
                logger.info("Key fields not provided for " + testName + ". Defaulting to all fields.");
                keyFields = getFields();
            } else {
                keyFields = StringUtil.splitAndTrim(keyHeader);
            }
        }

        return keyFields != null ? keyFields : null;
    }

    /**
     * @return the compareFields
     */
    public String[] getCompareFields() {
        if (compareFields == null) {
            // If keys are equal, all keyHeader fields should be equal, so comparison will only be done on non-keyHeader compare fields.
            final String compareHeader = getCompareHeader();
            if (compareHeader == null) {
                logger.info("Comparison fields not provided for " + testName + ". Defaulting to all fields.");
                compareFields = getFields();
            } else {
                // Got raw comparison fields. Can remove the key fields.
                final String[] headers = StringUtil.splitAndTrim(compareHeader);
                final List<String> compareFieldsList = new ArrayList<>(headers.length);
                Collections.addAll(compareFieldsList, headers);
                compareFieldsList.removeAll(Arrays.asList(getKeyFields()));
                compareFields = compareFieldsList.toArray(new String[compareFieldsList.size()]);
            }
        }
        return compareFields != null ? compareFields : null;
    }

    /**
     * @return the compareHeader
     */
    private String getCompareHeader() {
        return TaijituConfigImpl.getCompareFields(testName);
    }

    /**
     * @return the testName
     */
    public String getTestName() {
        return testName;
    }

    /**
     * @return the source
     */
    public Query getSource() {
        return source;
    }

    /**
     * @return the target
     */
    public Query getTarget() {
        return target;
    }

    /**
     * @return the precisionThreshold
     */
    public final double getPrecisionThreshold() {
        // lazy init
        if (precisionThreshold == null) {
            precisionThreshold = TaijituConfigImpl.getPrecisionThreshold(testName);
        }
        return precisionThreshold;
    }

    public ComparisonResult getResult() {
        return result;
    }

    public Map<Class<?>, Comparator<Object>> getComparators() {
        return comparators;
    }

    public ComparisonStrategy getStrategy() {
        return strategy;
    }


    public void calculateActualFields() throws TaijituException {
        try {
            final String[] newFields = ColumnDescriptionUtils.calculateActualFields(this.getFields(), getResult().getSourceColumnDescriptions(), getResult().getTargetColumnDescriptions());
            this.setFields(newFields);
        } catch (QueryUtilsException e) {
            throw new TaijituException("Unable to update fields with query data. ", e);
        }
    }

}
