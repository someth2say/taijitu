package org.someth2say.taijitu;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.config.ComparisonConfig;
import org.someth2say.taijitu.config.QueryConfig;
import org.someth2say.taijitu.matcher.FieldMatcher;
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
    private List<FieldDescription> canonicalFields;
    private List<String> canonicalKeys;
    private Map<String, FieldDescription[]> providedFieldsMap = new HashMap<>();

    public ComparisonRuntime(final ComparisonConfig comparisonConfig) {
        this.comparisonConfig = comparisonConfig;

        this.comparators = buildComparators();
    }

    //TODO: Move to ComparatorRegistry and ComparatorConfig
    private Map<Class<?>, Comparator<Object>> buildComparators() {
        Map<Class<?>, Comparator<Object>> res = new HashMap<>();


        return res;
    }

    public List<FieldDescription> getProvidedFields(String name) {
        return Arrays.asList(providedFieldsMap.get(name));
    }


    boolean registerFields(final FieldDescription[] providedFields, final QueryConfig queryConfig, final FieldMatcher fieldMatcher) {
        if (providedFields == null) return false;
        providedFieldsMap.put(queryConfig.getName(), providedFields);

        String[] providedKeys = queryConfig.getKeyFields();
        List<String> providedKeysList = Arrays.asList(providedKeys);
        List<FieldDescription> providedFieldsList = Arrays.asList(providedFields);

        if (validateKeyFieldsAreProvided(providedKeysList, providedFieldsList)) {
            if (canonicalFields == null) {
                canonicalFields = Arrays.asList(providedFields);
                canonicalKeys = providedKeysList;
                updateIndexes();
                return true;
            } else {
                if (validateCanonicalKeysAreProvided(queryConfig, fieldMatcher, providedKeysList, providedFieldsList)) {
                    shrinkCanonicalFieldsToProvided(fieldMatcher, providedFieldsList);
                    return true;
                }
            }
        }
        return false;
    }

    private int[] keyFieldIdxs;
    private int[] nonKeyFieldIndexes;

    private void updateIndexes() {
        keyFieldIdxs = new int[canonicalKeys.size()];
        nonKeyFieldIndexes = new int[canonicalFields.size() - canonicalKeys.size()];
        int keyPos = 0;
        int nonKeyPos = 0;
        for (int colPos = 0; colPos < canonicalFields.size(); colPos++) {
            String fieldName = canonicalFields.get(colPos).getName();
            if (canonicalKeys.contains(fieldName)) {
                keyFieldIdxs[keyPos++] = colPos;
            } else {
                nonKeyFieldIndexes[nonKeyPos++] = colPos;
            }
        }
    }

    public int[] getKeyFieldIdxs() {
        return keyFieldIdxs;
    }

    public int[] getNonKeyFieldsIdxs() {
        return nonKeyFieldIndexes;
    }

    private boolean validateCanonicalKeysAreProvided(final QueryConfig queryConfig, final FieldMatcher fieldMatcher, final List<String> providedKeysList, final List<FieldDescription> providedFieldsList) {
        // .- Both provided and canonical keys should be exactly the same
        ArrayList<String> canonicalProvidedKeyList = new ArrayList<>(providedKeysList.size());
        for (String providedKey : providedKeysList) {
            FieldDescription canonicalProvidedKey = fieldMatcher.getCanonicalFromField(providedKey, canonicalFields, providedFieldsList);
            if (canonicalProvidedKey == null) {
                logger.error("Key field " + providedKey + " in query " + queryConfig.getName() + " do not have a canonical match (using matching strategy " + fieldMatcher.getName() + ")");
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

    private boolean validateKeyFieldsAreProvided(List<String> providedKeysList, List<FieldDescription> providedFieldsList) {
        // .- Provided keys should be a subset for provide fields.
        return providedFieldsList.stream().map(FieldDescription::getName).collect(Collectors.toList()).containsAll(providedKeysList);
    }

    private void shrinkCanonicalFieldsToProvided(FieldMatcher fieldMatcher, List<FieldDescription> providedFieldsList) {
        // .- Canonical fields not provided are removed from comparison
        List<FieldDescription> canonicalProvidedFieldsList = new ArrayList<>(providedFieldsList.size());
        for (FieldDescription providedColum : providedFieldsList) {
            FieldDescription canonicalProvidedField = fieldMatcher.getCanonicalFromField(providedColum.getName(), canonicalFields, providedFieldsList);
            canonicalProvidedFieldsList.add(canonicalProvidedField);
        }
        canonicalFields.retainAll(canonicalProvidedFieldsList);
    }

    public List<FieldDescription> getCanonicalFields() {
        return canonicalFields;
    }

    public List<String> getCanonicalKeys() {
        return canonicalKeys;
    }


    public ComparisonConfig getComparisonConfig() {
        return comparisonConfig;
    }
}
