package org.someth2say.taijitu.matcher;

import org.someth2say.taijitu.tuple.FieldDescription;

import java.util.List;

/**
 * Returns the very same field. Assumes 'canonicalFields' and 'fields' are always the same.
 */

public class IdentityFieldMatcher implements FieldMatcher {

    public static final String NAME = "identity";

    @Override
    public FieldDescription getCanonicalFromField(FieldDescription field, List<FieldDescription> canonicalFields, List<FieldDescription> fields) {
        return canonicalFields.contains(field) ? field : null;
    }

    @Override
    public FieldDescription getFieldFromCanonical(FieldDescription canonicalField, List<FieldDescription> canonicalFields, List<FieldDescription> providedFields) {
        return providedFields.contains(canonicalField) ? canonicalField : null;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
