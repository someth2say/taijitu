package org.someth2say.taijitu.matcher;

import org.someth2say.taijitu.tuple.FieldDescription;

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
    public FieldDescription getCanonicalFromField(FieldDescription field, List<FieldDescription> canonicalFields, List<FieldDescription> fields) {
        return canonicalFields.get(fields.indexOf(field));
    }

    @Override
    public FieldDescription getFieldFromCanonical(FieldDescription canonicalField, List<FieldDescription> canonicalFields, List<FieldDescription> providedFields) {
        return providedFields.get(canonicalFields.indexOf(canonicalField));
    }
}
