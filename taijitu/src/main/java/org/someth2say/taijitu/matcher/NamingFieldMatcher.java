package org.someth2say.taijitu.matcher;

import org.someth2say.taijitu.source.FieldDescription;

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
    public FieldDescription getCanonicalField(FieldDescription providedField, List<FieldDescription> canonicalFields, List<FieldDescription> providedFields) {
        return canonicalFields.stream().filter(fieldDescription -> fieldDescription.getName().equals(providedField.getName())).findFirst().orElse(null);
    }

    @Override
    public FieldDescription getProvidedField(FieldDescription canonicalField, List<FieldDescription> canonicalFields, List<FieldDescription> providedFields) {
        return providedFields.stream().filter(fieldDescription -> fieldDescription.getName().equals(canonicalField.getName())).findFirst().orElse(null);
    }
}
