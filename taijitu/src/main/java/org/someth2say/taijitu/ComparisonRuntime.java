package org.someth2say.taijitu;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.config.ComparisonConfig;
import org.someth2say.taijitu.config.EqualityConfig;
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

    private final ComparisonConfig comparisonConfig;

    //TODO: This hurts! should be final...
    private List<FieldDescription> canonicalFields;
    private List<String> canonicalKeys;
    private List<EqualityConfig> equalityConfigs;

    private Map<String, FieldDescription[]> providedFieldsMap = new HashMap<>();

    public ComparisonRuntime(final ComparisonConfig comparisonConfig) {
        this.comparisonConfig = comparisonConfig;
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
                rebuildIndexes();
                updateEqualityConfigs();
                return true;
            } else {
                if (validateCanonicalKeysAreProvided(queryConfig, fieldMatcher, providedKeysList, providedFieldsList)) {
                    if (shrinkCanonicalFieldsToProvided(fieldMatcher, providedFieldsList)) {
                        rebuildIndexes();
                        //TODO: Maybe equalityConfigs can be shrink with fields...
                        updateEqualityConfigs();
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private void updateEqualityConfigs() {
        equalityConfigs = new ArrayList<>(canonicalFields.size());
        for (FieldDescription fieldDescription : canonicalFields) {
            equalityConfigs.add(getEqualityConfigFor(fieldDescription.getClazz(), fieldDescription.getName(), comparisonConfig.getEqualityConfigs()));
        }
    }

    private EqualityConfig getEqualityConfigFor(final String fieldClass, final String fieldName, final List<EqualityConfig> equalityConfigs) {

        Optional<EqualityConfig> perfectMatches = equalityConfigs.stream().filter(eq -> fieldNameMatch(fieldName, eq) && fieldClassMatch(fieldClass, eq)).findFirst();
        Optional<EqualityConfig> nameMatches = equalityConfigs.stream().filter(eq -> fieldNameMatch(fieldName, eq) && eq.getFieldClass() == null).findFirst();
        Optional<EqualityConfig> classMathes = equalityConfigs.stream().filter(eq -> eq.getFieldName() == null && fieldClassMatch(fieldClass, eq)).findFirst();
        Optional<EqualityConfig> allMathes = equalityConfigs.stream().filter(eq -> eq.getFieldName() == null && eq.getFieldClass() == null).findFirst();

        return perfectMatches.orElse(nameMatches.orElse(classMathes.orElse(allMathes.orElse(null))));

    }

    private boolean fieldNameMatch(String fieldName, EqualityConfig eq) {
        return eq.getFieldName() != null && fieldName.equals(eq.getFieldName());
    }

    private boolean fieldClassMatch(String fieldClassName, EqualityConfig eq) {
        String configClassName = eq.getFieldClass();
        if (configClassName == null) return false;
        if (eq.fieldClassStrict()) {
            return fieldClassName.equals(configClassName);
        } else {
            try {
                Class<?> configClass = Class.forName(configClassName);
                Class<?> fieldClass = Class.forName(fieldClassName);
                return configClass.isAssignableFrom(fieldClass);
            } catch (ClassNotFoundException e) {
                logger.error("Class defined in equality config not found: " + configClassName);
                return false;
            }
        }
    }


    private int[] keyFieldIdxs;
    private int[] nonKeyFieldIndexes;

    private void rebuildIndexes() {
        //TODO: Canonical key fields should not change, so do not need to be recalculated (assuming no key field may have been removed while shrink)
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

    private boolean shrinkCanonicalFieldsToProvided(FieldMatcher fieldMatcher, List<FieldDescription> providedFieldsList) {
        // .- Canonical fields not provided are removed from comparison
        List<FieldDescription> canonicalProvidedFieldsList = new ArrayList<>(providedFieldsList.size());
        for (FieldDescription providedColumn : providedFieldsList) {
            FieldDescription canonicalProvidedField = fieldMatcher.getCanonicalFromField(providedColumn.getName(), canonicalFields, providedFieldsList);
            canonicalProvidedFieldsList.add(canonicalProvidedField);
        }
        return canonicalFields.retainAll(canonicalProvidedFieldsList);
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

    public List<EqualityConfig> getEqualityConfigs() {
        return equalityConfigs;
    }
}
