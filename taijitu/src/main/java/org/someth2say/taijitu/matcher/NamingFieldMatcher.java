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
    public FieldDescription getCanonicalFromField(String field, List<FieldDescription> canonicalFields, List<FieldDescription> fields) {
        for (FieldDescription fieldDescription : canonicalFields) {
            if (fieldDescription.getName().equals(field))
                return fieldDescription;

        }
        return null;
    }

    @Override
    public String getFieldFromCanonical(FieldDescription canonicalField, List<FieldDescription> canonicalFields, List<FieldDescription> fields) {
        return canonicalFields.contains(canonicalField) ? canonicalField.getName() : null;
    }


}
