package org.someth2say.taijitu;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.someth2say.taijitu.config.ComparisonConfig;
import org.someth2say.taijitu.config.EqualityConfig;
import org.someth2say.taijitu.config.SourceConfig;
import org.someth2say.taijitu.matcher.FieldMatcher;
import org.someth2say.taijitu.tuple.FieldDescription;

import java.util.*;

/**
 * @author Jordi Sola
 * This class will keep all data that may be pre-computed before comparison: canonical fields, indexes, equality configurations...
 */
public class ComparisonContext {
    private static final Logger logger = Logger.getLogger(ComparisonContext.class);

    private final ComparisonConfig comparisonConfig;

    //TODO: This hurts! should be final...
    private List<FieldDescription> canonicalFields;
    private List<FieldDescription> canonicalKeys;
    private List<EqualityConfig> equalityConfigs;

    private Map<String, List<FieldDescription>> providedFieldsMap = new HashMap<>();

    public ComparisonContext(final ComparisonConfig comparisonConfig) {
        this.comparisonConfig = comparisonConfig;
    }

    public List<FieldDescription> getProvidedFields(String name) {
        return providedFieldsMap.get(name);
    }


    boolean registerFields(final List<FieldDescription> providedFields, final SourceConfig querySourceConfig, final FieldMatcher fieldMatcher) {
        if (providedFields == null) {
            return false;
        }

        providedFieldsMap.put(querySourceConfig.getName(), providedFields);

        List<FieldDescription> providedKeyFields = getKeyFieldsFromConfig(querySourceConfig, providedFields);
        if (providedKeyFields != null) {
            if (canonicalFields == null) {
                // Have no canonical fields -> First source, interpreted as canonical.
                canonicalFields = providedFields;
                canonicalKeys = providedKeyFields;
                rebuildIndexes();
                updateEqualityConfigs();
            } else {
                // Already have canonical fields, so should shrink
                if (shrinkCanonicalFieldsToProvided(fieldMatcher, providedFields)) {
                    rebuildIndexes();
                    //TODO: Maybe equalityConfigs can be shrink with fields...
                    updateEqualityConfigs();
                }
            }
            return true;
        } else {
            // Null provided key fields means that some key field have not been provided.
            logger.error("Not all key fields have been provided in " + querySourceConfig.getName() + " Provided: " + StringUtils.join(providedFields, ",") + " Keys: " + StringUtils.join(querySourceConfig.getKeyFields(), ","));
            return false;
        }
    }

    private List<FieldDescription> getKeyFieldsFromConfig(SourceConfig querySourceConfig, List<FieldDescription> providedFields) {
        List<String> configKeyFields = querySourceConfig.getKeyFields();
        List<FieldDescription> result = new ArrayList<>(configKeyFields.size());
        for (String configKeyField : configKeyFields) {
            Optional<FieldDescription> keyField = providedFields.stream().filter(fieldDescription -> fieldDescription.getName().equals(configKeyField)).findFirst();
            if (keyField.isPresent()) {
                result.add(keyField.get());
            } else {
                logger.error("Key field " + configKeyField + " is not  provided in " + querySourceConfig.getName() + " Provided: " + StringUtils.join(providedFields, ","));
                return null;
            }
        }
        return result;
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
        for (int canonicalFieldPos = 0; canonicalFieldPos < canonicalFields.size(); canonicalFieldPos++) {
            FieldDescription canonicalField = canonicalFields.get(canonicalFieldPos);
            if (canonicalKeys.contains(canonicalField)) {
                keyFieldIdxs[keyPos++] = canonicalFieldPos;
            } else {
                nonKeyFieldIndexes[nonKeyPos++] = canonicalFieldPos;
            }
        }
    }

    public int[] getKeyFieldIdxs() {
        return keyFieldIdxs;
    }

    public int[] getNonKeyFieldsIdxs() {
        return nonKeyFieldIndexes;
    }

    private boolean shrinkCanonicalFieldsToProvided(FieldMatcher fieldMatcher, List<FieldDescription> providedFieldsList) {
        // .- Canonical fields not provided are removed from comparison
        List<FieldDescription> canonicalProvidedFieldsList = new ArrayList<>(providedFieldsList.size());
        for (FieldDescription providedColumn : providedFieldsList) {
            FieldDescription canonicalProvidedField = fieldMatcher.getCanonicalFromField(providedColumn, canonicalFields, providedFieldsList);
            canonicalProvidedFieldsList.add(canonicalProvidedField);
        }
        return canonicalFields.retainAll(canonicalProvidedFieldsList);
    }

    public List<FieldDescription> getCanonicalFields() {
        return canonicalFields;
    }

    public List<FieldDescription> getCanonicalKeys() {
        return canonicalKeys;
    }

    public ComparisonConfig getComparisonConfig() {
        return comparisonConfig;
    }

    public List<EqualityConfig> getEqualityConfigs() {
        return equalityConfigs;
    }
}
