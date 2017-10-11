package org.someth2say.taijitu;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.config.ComparisonConfig;
import org.someth2say.taijitu.config.QueryConfig;
import org.someth2say.taijitu.matcher.ColumnMatcher;
import org.someth2say.taijitu.query.Query;
import org.someth2say.taijitu.query.QueryUtilsException;
import org.someth2say.taijitu.registry.MatcherRegistry;

import java.util.*;

/**
 * @author Jordi Sola
 * This class keep all values defined for a single comparison, as per in configuration file.
 */
//TODO: This class will die, as runtime data should be kept by the comparison runner (thread)
public class ComparisonRuntime {
    private static final Logger logger = Logger.getLogger(ComparisonRuntime.class);

    private final Query source;
    private final Query target;

    private final Map<Class<?>, Comparator<Object>> comparators;
    private final ComparisonConfig comparisonConfig;

    //TODO: This hurts! should be final...
    private List<String> canonicalColumns;
    private List<String> canonicalKeys;
    private final ColumnMatcher columnMatcher;

    public ComparisonRuntime(final ComparisonConfig comparisonConfig) throws TaijituException {
        this.comparisonConfig = comparisonConfig;
        source = buildQuery(comparisonConfig.getSourceQueryConfig());
        target = buildQuery(comparisonConfig.getTargetQueryConfig());

        this.comparators = buildComparators();

        this.columnMatcher = buildColumnMatcher(comparisonConfig);

    }

    private ColumnMatcher buildColumnMatcher(ComparisonConfig comparisonConfig) throws TaijituException {
        return MatcherRegistry.getMatcher(comparisonConfig.getColumnMatcher());
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


    private Map<String, String[]> providedColumnsMap = new HashMap<>();
    public List<String> getProvidedColumns(String name) {
        return Arrays.asList(providedColumnsMap.get(name));
    }


    public void registerColumns(final String[] providedColumns, final QueryConfig queryConfig) throws QueryUtilsException {
        providedColumnsMap.put(queryConfig.getName(), providedColumns);

        String[] providedKeys = queryConfig.getKeyFields();
        List<String> providedKeysList = Arrays.asList(providedKeys);
        List<String> providedColumnsList = Arrays.asList(providedColumns);

        if (canonicalColumns == null) {
            validateKeyColumnsAreProvided(queryConfig, providedKeysList, providedColumnsList);
            canonicalColumns = Arrays.asList(providedColumns);
            canonicalKeys = providedKeysList;
        } else {
            validateKeyColumnsAreProvided(queryConfig, providedKeysList, providedColumnsList);
            validateCanonicalKeysAreProvided(queryConfig, columnMatcher, providedKeysList, providedColumnsList);
            shrinkCanonicalColumnsToProvided(columnMatcher, providedColumnsList);
        }
    }

    private void validateCanonicalKeysAreProvided(final QueryConfig queryConfig, final ColumnMatcher columnMatcher, final List<String> providedKeysList, final List<String> providedColumnsList) throws QueryUtilsException {
        // .- Both provided and canonical keys should be exactly the same
        ArrayList<String> canonicalProvidedKeyList = new ArrayList<>(providedKeysList.size());
        for (String providedKey : providedKeysList) {
            String canonicalProvidedKey = columnMatcher.getMatchingColumn(providedKey, canonicalColumns, providedColumnsList);
            if (canonicalProvidedKey == null) {
                throw new QueryUtilsException("Key column " + providedKey + " in query " + queryConfig.getName() + " do not have a canonical match (using matching strategy " + columnMatcher.getName() + ")");
            } else {
                canonicalProvidedKeyList.add(canonicalProvidedKey);
            }
        }
        if (!canonicalKeys.equals(canonicalProvidedKeyList)) {
            throw new QueryUtilsException("Keys in query " + queryConfig.getName() + " must match (same amount, same order) canonical keys. " + canonicalProvidedKeyList.toString() + " vs " + canonicalKeys.toString());
        }
    }

    private void validateKeyColumnsAreProvided(QueryConfig queryConfig, List<String> providedKeysList, List<String> providedColumnsList) throws QueryUtilsException {
        // .- Provided keys should be a subset for provide columns.
        for (String providedKey : providedKeysList) {
            if (!providedColumnsList.contains(providedKey)) {
                throw new QueryUtilsException("Key " + providedKey + " in query " + queryConfig.getName() + " is not provided by query results.");
            }
        }
    }

    private void shrinkCanonicalColumnsToProvided(ColumnMatcher columnMatcher, List<String> providedColumnsList) {
        // .- Canonical columns not provided are removed from comparison
        List<String> canonicalProvidedColumnsList = new ArrayList<>(providedColumnsList.size());
        for (String providedColum : providedColumnsList) {
            String canonicalProvidedColumn = columnMatcher.getMatchingColumn(providedColum, canonicalColumns, providedColumnsList);
            canonicalProvidedColumnsList.add(canonicalProvidedColumn);
        }
        canonicalColumns.retainAll(canonicalProvidedColumnsList);
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
