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
    public FieldDescription getCanonicalFromField(String field, List<FieldDescription> canonicalFields, List<FieldDescription> fields) {
        int index = fields.indexOf(field);
        if (index >= 0 && index < canonicalFields.size()) {
            return canonicalFields.get(index);
        }
        return null;
    }

    @Override
    public String getFieldFromCanonical(FieldDescription canonicalField, List<FieldDescription> canonicalFields, List<FieldDescription> fields) {
        int index = canonicalFields.indexOf(canonicalField);
        if (index >= 0 && index < fields.size()) {
            return fields.get(index).getName();
        }
        return null;
    }
}
