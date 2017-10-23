package org.someth2say.taijitu.matcher;

import org.someth2say.taijitu.tuple.FieldDescription;

import java.util.List;

/**
 * Returns the very same field. Assumes 'canonicalFields' and 'fields' are always the same.
 */

public class IdentityFieldMatcher implements FieldMatcher {

    public static final String NAME = "identity";

    @Override
    public FieldDescription getCanonicalFromField(final String field, final List<FieldDescription> canonicalFields, final List<FieldDescription> fields) {
        return canonicalFields.stream().filter(fd -> fd.getName().equals(field)).findFirst().get();
    }

    @Override
    public String getFieldFromCanonical(final FieldDescription canonicalField, final List<FieldDescription> canonicalFields, final List<FieldDescription> fields) {
        return canonicalField.getName();
    }

    @Override
    public String getName() {
        return NAME;
    }
}
