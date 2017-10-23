package org.someth2say.taijitu;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.someth2say.taijitu.config.ComparisonConfig;
import org.someth2say.taijitu.config.QueryConfig;
import org.someth2say.taijitu.matcher.ColumnMatcher;
import org.someth2say.taijitu.tuple.FieldDescription;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Jordi Sola
 * This class keep all values defined for a single comparison, as per in configuration file.
 */
//TODO: This class will die, as runtime data should be kept by the comparison runner (thread)
public class ComparisonRuntime {
    private static final Logger logger = Logger.getLogger(ComparisonRuntime.class);

    private final Map<Class<?>, Comparator<Object>> comparators;
    private final ComparisonConfig comparisonConfig;

    //TODO: This hurts! should be final...
    private List<FieldDescription> canonicalColumns;
    private List<String> canonicalKeys;
    private Map<String, FieldDescription[]> providedColumnsMap = new HashMap<String, FieldDescription[]>();

    public ComparisonRuntime(final ComparisonConfig comparisonConfig) {
        this.comparisonConfig = comparisonConfig;

        this.comparators = buildComparators();
    }

    //TODO: Move to ComparatorRegistry and ComparatorConfig
    private Map<Class<?>, Comparator<Object>> buildComparators() {
        Map<Class<?>, Comparator<Object>> res = new HashMap<>();


        return res;
    }

    public List<FieldDescription> getProvidedColumns(String name) {
        return Arrays.asList(providedColumnsMap.get(name));
    }


    boolean registerColumns(final FieldDescription[] providedColumns, final QueryConfig queryConfig, final ColumnMatcher columnMatcher) {
        if (providedColumns == null) return false;
        providedColumnsMap.put(queryConfig.getName(), providedColumns);

        String[] providedKeys = queryConfig.getKeyFields();
        List<String> providedKeysList = Arrays.asList(providedKeys);
        List<FieldDescription> providedColumnsList = Arrays.asList(providedColumns);

        if (validateKeyColumnsAreProvided(queryConfig, providedKeysList, providedColumnsList)) {
            if (canonicalColumns == null) {
                canonicalColumns = Arrays.asList(providedColumns);
                canonicalKeys = providedKeysList;
                updateIndexes();
                return true;
            } else {
                if (validateCanonicalKeysAreProvided(queryConfig, columnMatcher, providedKeysList, providedColumnsList)) {
                    shrinkCanonicalColumnsToProvided(columnMatcher, providedColumnsList);
                    return true;
                }
            }
        }
        return false;
    }

    private int[] keyColumnIndexes;
    private int[] nonKeyColumnIndexes;

    private void updateIndexes() {
        keyColumnIndexes = new int[canonicalKeys.size()];
        nonKeyColumnIndexes = new int[canonicalColumns.size() - canonicalKeys.size()];
        int keyPos = 0;
        int nonKeyPos = 0;
        for (int colPos = 0; colPos < canonicalColumns.size(); colPos++) {
            String columnName = canonicalColumns.get(colPos).getName();
            if (canonicalKeys.contains(columnName)) {
                keyColumnIndexes[keyPos++] = colPos;
            } else {
                nonKeyColumnIndexes[nonKeyPos++] = colPos;
            }
        }
    }

    public int[] getKeyColumnsIdxs() {
        return keyColumnIndexes;
    }

    public int[] getNonKeyColumnsIdxs() {
        return nonKeyColumnIndexes;
    }

    private boolean validateCanonicalKeysAreProvided(final QueryConfig queryConfig, final ColumnMatcher columnMatcher, final List<String> providedKeysList, final List<FieldDescription> providedColumnsList) {
        // .- Both provided and canonical keys should be exactly the same
        ArrayList<String> canonicalProvidedKeyList = new ArrayList<>(providedKeysList.size());
        for (String providedKey : providedKeysList) {
            FieldDescription canonicalProvidedKey = columnMatcher.getCanonicalFromColumn(providedKey, canonicalColumns, providedColumnsList);
            if (canonicalProvidedKey == null) {
                logger.error("Key column " + providedKey + " in query " + queryConfig.getName() + " do not have a canonical match (using matching strategy " + columnMatcher.getName() + ")");
                return false;
            } else {
                canonicalProvidedKeyList.add(canonicalProvidedKey.getName());
            }
        }
        if (!canonicalKeys.equals(canonicalProvidedKeyList)) {
            logger.error("Keys in query " + queryConfig.getName() + " must match (same amount, same order) canonical keys. " + canonicalProvidedKeyList.toString() + " vs " + canonicalKeys.toString());
            return false;
        }
        return true;
    }

    private boolean validateKeyColumnsAreProvided(QueryConfig queryConfig, List<String> providedKeysList, List<FieldDescription> providedColumnsList) {
        // .- Provided keys should be a subset for provide columns.
        return providedColumnsList.stream().map(FieldDescription::getName).collect(Collectors.toList()).containsAll(providedKeysList);
    }

    private void shrinkCanonicalColumnsToProvided(ColumnMatcher columnMatcher, List<FieldDescription> providedColumnsList) {
        // .- Canonical columns not provided are removed from comparison
        List<FieldDescription> canonicalProvidedColumnsList = new ArrayList<>(providedColumnsList.size());
        for (FieldDescription providedColum : providedColumnsList) {
            FieldDescription canonicalProvidedColumn = columnMatcher.getCanonicalFromColumn(providedColum.getName(), canonicalColumns, providedColumnsList);
            canonicalProvidedColumnsList.add(canonicalProvidedColumn);
        }
        canonicalColumns.retainAll(canonicalProvidedColumnsList);
    }

    public List<FieldDescription> getCanonicalColumns() {
        return canonicalColumns;
    }

    public List<String> getCanonicalKeys() {
        return canonicalKeys;
    }


    public ComparisonConfig getComparisonConfig() {
        return comparisonConfig;
    }
}
