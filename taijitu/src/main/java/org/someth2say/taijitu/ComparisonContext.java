package org.someth2say.taijitu;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.someth2say.taijitu.config.interfaces.IComparisonCfg;
import org.someth2say.taijitu.config.interfaces.IEqualityCfg;
import org.someth2say.taijitu.config.interfaces.ISourceCfg;
import org.someth2say.taijitu.matcher.FieldMatcher;
import org.someth2say.taijitu.tuple.FieldDescription;

import java.util.*;

/**
 * @author Jordi Sola
 * This class will keep all data that may be pre-computed before comparison: canonical fields, indexes, equality configurations...
 */
public class ComparisonContext {
    private static final Logger logger = Logger.getLogger(ComparisonContext.class);

    private final IComparisonCfg comparisonConfigIface;

    //TODO: This hurts! should be final...
    private List<FieldDescription> canonicalFields;
    private List<FieldDescription> canonicalKeys;
    private List<IEqualityCfg> equalityConfigIfaces;

    private Map<String, List<FieldDescription>> providedFieldsMap = new HashMap<>();

    public ComparisonContext(final IComparisonCfg comparisonConfigIface) {
        this.comparisonConfigIface = comparisonConfigIface;
    }

    public List<FieldDescription> getProvidedFields(String name) {
        return providedFieldsMap.get(name);
    }


    boolean registerFields(final List<FieldDescription> providedFields, final ISourceCfg querySourceConfigIface, final FieldMatcher fieldMatcher) {
        if (providedFields == null) {
            return false;
        }

        providedFieldsMap.put(querySourceConfigIface.getName(), providedFields);

        List<FieldDescription> providedKeyFields = getKeyFieldsFromConfig(querySourceConfigIface, providedFields);
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
                    //TODO: Maybe equalityConfigIfaces can be shrink with fields...
                    updateEqualityConfigs();
                }
            }
            return true;
        } else {
            // Null provided key fields means that some key field have not been provided.
            logger.error("Not all key fields have been provided in " + querySourceConfigIface.getName() + " Provided: " + StringUtils.join(providedFields, ",") + " Keys: " + StringUtils.join(querySourceConfigIface.getKeyFields(), ","));
            return false;
        }
    }

    private List<FieldDescription> getKeyFieldsFromConfig(ISourceCfg querySourceConfigIface, List<FieldDescription> providedFields) {
        List<String> configKeyFields = querySourceConfigIface.getKeyFields();
        List<FieldDescription> result = new ArrayList<>(configKeyFields.size());
        for (String configKeyField : configKeyFields) {
            Optional<FieldDescription> keyField = providedFields.stream().filter(fieldDescription -> fieldDescription.getName().equals(configKeyField)).findFirst();
            if (keyField.isPresent()) {
                result.add(keyField.get());
            } else {
                logger.error("Key field " + configKeyField + " is not  provided in " + querySourceConfigIface.getName() + " Provided: " + StringUtils.join(providedFields, ","));
                return null;
            }
        }
        return result;
    }

    private void updateEqualityConfigs() {
        equalityConfigIfaces = new ArrayList<>(canonicalFields.size());
        for (FieldDescription fieldDescription : canonicalFields) {
            equalityConfigIfaces.add(getEqualityConfigFor(fieldDescription.getClazz(), fieldDescription.getName(), comparisonConfigIface.getEqualityConfigs()));
        }
    }

    private IEqualityCfg getEqualityConfigFor(final String fieldClass, final String fieldName, final List<IEqualityCfg> equalityConfigIfaces) {

        Optional<IEqualityCfg> perfectMatches = equalityConfigIfaces.stream().filter(eq -> fieldNameMatch(fieldName, eq) && fieldClassMatch(fieldClass, eq)).findFirst();
        Optional<IEqualityCfg> nameMatches = equalityConfigIfaces.stream().filter(eq -> fieldNameMatch(fieldName, eq) && eq.getFieldClass() == null).findFirst();
        Optional<IEqualityCfg> classMathes = equalityConfigIfaces.stream().filter(eq -> eq.getFieldName() == null && fieldClassMatch(fieldClass, eq)).findFirst();
        Optional<IEqualityCfg> allMathes = equalityConfigIfaces.stream().filter(eq -> eq.getFieldName() == null && eq.getFieldClass() == null).findFirst();

        return perfectMatches.orElse(nameMatches.orElse(classMathes.orElse(allMathes.orElse(null))));

    }

    private boolean fieldNameMatch(String fieldName, IEqualityCfg eq) {
        return eq.getFieldName() != null && fieldName.equals(eq.getFieldName());
    }

    private boolean fieldClassMatch(String fieldClassName, IEqualityCfg eq) {
        if (fieldClassName == null) return false;
        String configClassName = eq.getFieldClass();
        if (configClassName == null) return false;
        if (eq.isFieldClassStrict()) {
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

    public IComparisonCfg getComparison() {
        return comparisonConfigIface;
    }

    public List<IEqualityCfg> getIEqualitys() {
        return equalityConfigIfaces;
    }
}
