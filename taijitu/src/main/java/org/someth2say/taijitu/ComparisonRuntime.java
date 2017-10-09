package org.someth2say.taijitu;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.config.ComparisonConfig;
import org.someth2say.taijitu.config.ConfigurationLabels;
import org.someth2say.taijitu.config.QueryConfig;
import org.someth2say.taijitu.config.TaijituConfigImpl;
import org.someth2say.taijitu.query.Query;
import org.someth2say.taijitu.query.QueryUtilsException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jordi Sola
 * This class keep all values defined for a single comparison, as per in configuration file.
 */
//TODO: This class will die, as runtime data should be kept by the comparison runner (thread)
public class ComparisonRuntime {
    final String queryParamRegexp = "\\{(.*?)}";

    private static final Logger logger = Logger.getLogger(ComparisonRuntime.class);
    private final Query source;
    private final Query target;

    private final Map<Class<?>, Comparator<Object>> comparators;
    private final ComparisonConfig comparisonConfig;

    public ComparisonRuntime(final ComparisonConfig comparisonConfig) throws TaijituException, QueryUtilsException {
        this.comparisonConfig = comparisonConfig;
        source = buildQuery(comparisonConfig.getSourceQueryConfig());
        target = buildQuery(comparisonConfig.getTargetQueryConfig());

        this.comparators = buildComparators();

    }


    private Query buildQuery(final QueryConfig queryConfig) throws TaijituException, QueryUtilsException {

        String queryStr = queryConfig.getStatement();
        final List<Object> queryParameterValues = prepareParameterValues(queryConfig);
        final String replacedQueryStr = replaceQueryParameterTags(queryStr);

        return new Query(replacedQueryStr, queryParameterValues, queryConfig.getFetchSize());

    }


    /**
     * Replace parameters by '?' in order to be assigned in JDBC queries
     *
     * @param query String for the query
     * @return Same query string, but with all parameter tags replaces by ?
     */
    private String replaceQueryParameterTags(final String query) {

        final Pattern parameterPattern = Pattern.compile(queryParamRegexp);
        final Matcher parameterMatcher = parameterPattern.matcher(query);
        return parameterMatcher.replaceAll("?");
    }

    /**
     * Generate the list of parameter values, in the same order as they appear on the query.
     * Parameter values are take from configuration properties.
     *
     * @param queryConfig The query string
     * @return The list of values to be used for query parameters
     */
    private List<Object> prepareParameterValues(final QueryConfig queryConfig) throws TaijituException {
        final List<Object> values = new ArrayList<>();
        // 1) Store parameters in query order
        final Pattern parameterPattern = Pattern.compile(queryParamRegexp);
        final Matcher parameterMatcher = parameterPattern.matcher(queryConfig.getStatement());
        while (parameterMatcher.find()) {
            final String parameterTag = parameterMatcher.group();
            final String parameterName = parameterTag.substring(1, parameterTag.length() - 1);

            String parameterValue = queryConfig.getParameter(parameterName);

            if (parameterTag.contains(ConfigurationLabels.DATE_PARAMETER_KEYWORD)) {
                values.add(TaijituConfigImpl.parseDate(parameterValue));
            } else {
                values.add(parameterValue);
            }
        }

        return values;
    }


    //TODO: Move to ComparatorRegistry and ComparatorConfig
    private Map<Class<?>, Comparator<Object>> buildComparators() {
        Map<Class<?>, Comparator<Object>> res = new HashMap<>();
//        final double threshold = getPrecisionThreshold();
//        if (threshold > 0) {
//            res.put(BigDecimal.class, new PrecissionThresholdComparator(threshold));
//        }
        return res;
    }

    public void registerColumns(final String[] sourceColumns, final String[] targetColumns, final ComparisonConfig comparisonConfig) {


    }

    // Can't be final, 'cause it is mutable >:(
    private List<String> canonicalColumns = null;
    private List<String> canonicalKeys = null;

    // TODO: What about the keys? What if a key field does not survive?
    public void registerColumns(final String[] columns, final QueryConfig queryConfig, final ComparisonRuntime comparisonRuntime) throws QueryUtilsException {
        // Should use columnMatchingStrategy to find what fields do actually survive!
        ColumnMatchingStrategy columnMatchingStrategy = comparisonRuntime.getColumnMatchingStrategy();

        if (canonicalColumns == null) {
            // Runtime columns
            canonicalColumns = Arrays.asList(columns);
            //Runtime key
            canonicalKeys = Arrays.asList(queryConfig.getKeyFields());

            validateKeyColumns(queryConfig, columnMatchingStrategy);

        } else {
            List<String> configKeys = Arrays.asList(queryConfig.getKeyFields());
            List<String> columnsList = Arrays.asList(columns);
            for (String column : columns) {

                // KEYS
                //1.- Key should be provided by query
                //2.- Key should have a canonical match

                // NON-KEYS
                //1.- If column is not key, and have no canonical match, it is just ignored (i.e. query provides more information than needed)
                //2.- If a canonical non-key column is not provided, it is removed from the list of canonical columns (i.e. query does not provide some information).

                if (configKeys.contains(column)) {
                    //KEY
                    if (!columnsList.contains(column)) {
                        throw new QueryUtilsException("Key " + column" in query " + queryConfig.getName() + " is not provided by query results.");
                    }
                    String cannonicalKey = columnMatchingStrategy.getMatchingColumn(column);
                    if (!canonicalKeys.contains(cannonicalKey)) {
                        throw new QueryUtilsException("Key " + column" in query " + queryConfig.getName() + " do not have a canonical match (using matching strategy " + columnMatchingStrategy.getName() + ")");
                    }
                } else {
                    // NON-KEY
                    String canonicalColumn = columnMatchingStrategy.getMatchingColumn(column);
                    if (!canonicalColumns.contains(canonicalColumn)) {
                        logger.warn("Column " + column + " in query " + queryConfig.getName() + " do not have a canonical match (using matching strategy " + columnMatchingStrategy.getName() + ")");
                        canonicalColumns.remove(canonicalColumn);
                    }
                }
















            }


            List<String> matchingColumns = new ArrayList<>(canonicalColumns.size());

            for (int runtimeColumnPos = 0; runtimeColumnPos < canonicalColumns.size(); runtimeColumnPos++) {
                if (columnMatchingStrategy.isColumnMatching(canonicalColumns, runtimeColumnPos, columns)) {
                    //Column actually have a match, so it survives.
                    matchingColumns.add(canonicalColumns.get(runtimeColumnPos));
                } else {
                    logger.info("Column '" + canonicalColumns.get(runtimeColumnPos) + "' does not have matching column, so will not be considered for comparison.");
                }
            }
            canonicalColumns = matchingColumns;
            validateKeyColumns(queryConfig, columnMatchingStrategy);
        }
    }

    private void validateKeyColumns(QueryConfig queryConfig, ColumnMatchingStrategy columnMatchingStrategy) throws QueryUtilsException {
        String[] queryConfigKeys = queryConfig.getKeyFields();
        for (String configKey : queryConfigKeys) {
            String canonicalKey = columnMatchingStrategy.getMatchingColumn(configKey);
            //1.- Configured key column should be provided by the query
            if (!canonicalColumns.contains(canonicalKey)) {
                throw new QueryUtilsException("Column " + configKey + "defined by " + queryConfig.getName() + " have no matching canonical column!");
            }

            //2.- Configured key column should have a match with canonical keys.
            if (!canonicalKeys.contains(canonicalKey)) {
                throw new QueryUtilsException("Column " + configKey + "defined by " + queryConfig.getName() + " have no matching canonical key!");
            }
        }
    }

    public List<String> getCanonicalColumns() {
        return canonicalColumns;
    }

    public List<String> getCanonicalKeys() {
        return canonicalKeys;
    }

    /**
     * Retrieve the indexes for the key columns on source query
     *
     * @param sourceQueryConfig
     * @param columns
     * @return
     */
    public int[] getSourceKeyFieldsIdxs(QueryConfig sourceQueryConfig) {
        List<String> keyFields = getCanonicalKeys();
        int[] result = new int[keyFields.size()];
        for (int keyFieldIdx = 0; keyFieldIdx < keyFields.size(); keyFieldIdx++) {
            // Source query does not need field matching, so we can directly look for the name in the column list.


        }
        return result;
    }

//
//    /**
//     * @return the header
//     */
//    private String getHeader() {
//        if (header == null) {
//            header = TaijituConfigImpl.getHeader(this.testName);
//        }
//        return header;
//    }
//
//    public String[] getFields() {
//        if (fields == null) {
//            final String headerStr = getHeader();
//            if (headerStr != null) {
//                fields = StringUtil.splitAndTrim(headerStr);
//            }
//        }
//        return fields != null ? fields : null;
//    }
//
//    public void setFields(final String[] _fields) {
//        this.fields = _fields;
//        //as key fields and compare fields depend on fields, those should be updated
//        this.keyFields = null;
//        this.compareFields = null;
//    }
//
//    /**
//     * @return the keyFields
//     */
//    public String[] getKeyFields() {
//        if (keyFields == null) {
//            String keyHeader = TaijituConfigImpl.getKeyFields(testName);
//            if (keyHeader == null) {
//                logger.info("Key fields not provided for " + testName + ". Defaulting to all fields.");
//                keyFields = getFields();
//            } else {
//                keyFields = StringUtil.splitAndTrim(keyHeader);
//            }
//        }
//
//        return keyFields != null ? keyFields : null;
//    }
//
//    /**
//     * @return the compareFields
//     */
//    public String[] getCompareFields() {
//        if (compareFields == null) {
//            // If keys are equal, all keyHeader fields should be equal, so comparison will only be done on non-keyHeader compare fields.
//            final String compareHeader = getCompareHeader();
//            if (compareHeader == null) {
//                logger.info("Comparison fields not provided for " + testName + ". Defaulting to all fields.");
//                compareFields = getFields();
//            } else {
//                // Got raw comparison fields. Can remove the key fields.
//                final String[] headers = StringUtil.splitAndTrim(compareHeader);
//                final List<String> compareFieldsList = new ArrayList<>(headers.length);
//                Collections.addAll(compareFieldsList, headers);
//                compareFieldsList.removeAll(Arrays.asList(getKeyFields()));
//                compareFields = compareFieldsList.toArray(new String[compareFieldsList.size()]);
//            }
//        }
//        return compareFields != null ? compareFields : null;
//    }
//
//    /**
//     * @return the compareHeader
//     */
//    private String getCompareHeader() {
//        return TaijituConfigImpl.getCompareFields(testName);
//    }
//
//    /**
//     * @return the testName
//     */
//    public String getTestName() {
//        return testName;
//    }
//
//    /**
//     * @return the source
//     */
//    public Query getSource() {
//        return source;
//    }
//
//    /**
//     * @return the target
//     */
//    public Query getTarget() {
//        return target;
//    }
//
//    /**
//     * @return the precisionThreshold
//     */
//    public final double getPrecisionThreshold() {
//        // lazy init
//        if (precisionThreshold == null) {
//            precisionThreshold = TaijituConfigImpl.getPrecisionThreshold(testName);
//        }
//        return precisionThreshold;
//    }
//
//    public ComparisonResult getResult() {
//        return result;
//    }
//
//    public Map<Class<?>, Comparator<Object>> getComparators() {
//        return comparators;
//    }
//
//    public ComparisonStrategy getStrategy() {
//        return strategy;
//    }
//
//
//    public void calculateActualFields() throws TaijituException {
//        try {
//            final String[] newFields = ColumnDescriptionUtils.calculateActualFields(this.getFields(), getResult().getSourceColumnDescriptions(), getResult().getTargetColumnDescriptions());
//            this.setFields(newFields);
//        } catch (QueryUtilsException e) {
//            throw new TaijituException("Unable to update fields with query data. ", e);
//        }
//    }

}
