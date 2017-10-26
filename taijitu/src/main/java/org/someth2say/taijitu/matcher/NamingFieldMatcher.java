package org.someth2say.taijitu.matcher;

import org.someth2say.taijitu.tuple.FieldDescription;

import java.util.List;

/**
 * Matches fields by name. That is, returns the field in 'matching' list that have the same name than the 'field'.
 */
public class NamingFieldMatcher implements FieldMatcher {

    public static final String NAME = "naming";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public FieldDescription getCanonicalFromField(FieldDescription field, List<FieldDescription> canonicalFields, List<FieldDescription> fields) {
        return canonicalFields.stream().filter(fieldDescription -> fieldDescription.getName().equals(field.getName())).findFirst().orElse(null);
    }

    @Override
    public FieldDescription getFieldFromCanonical(FieldDescription canonicalField, List<FieldDescription> canonicalFields, List<FieldDescription> providedFields) {
        return providedFields.stream().filter(fieldDescription -> fieldDescription.getName().equals(canonicalField.getName())).findFirst().orElse(null);
    }
}
