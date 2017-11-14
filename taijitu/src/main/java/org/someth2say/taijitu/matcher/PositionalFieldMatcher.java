package org.someth2say.taijitu.matcher;

import org.someth2say.taijitu.source.FieldDescription;

import java.util.List;

/**
 * Matches fields by position. That is, returns the field in 'matching' in the same position than 'field' in 'fields' list.
 */
public class PositionalFieldMatcher implements FieldMatcher {
    public static final String NAME = "position";

    @Override
    public String getName() {
        return PositionalFieldMatcher.NAME;
    }

    @Override
    public FieldDescription getCanonicalField(FieldDescription providedField, List<FieldDescription> canonicalFields, List<FieldDescription> providedFields) {
        return canonicalFields.get(providedFields.indexOf(providedField));
    }

    @Override
    public FieldDescription getProvidedField(FieldDescription canonicalField, List<FieldDescription> canonicalFields, List<FieldDescription> providedFields) {
        return providedFields.get(canonicalFields.indexOf(canonicalField));
    }
}
