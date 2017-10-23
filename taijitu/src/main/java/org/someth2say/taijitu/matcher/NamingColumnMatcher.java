package org.someth2say.taijitu.matcher;

import org.someth2say.taijitu.tuple.FieldDescription;

import java.util.List;

/**
 * Matches columns by name. That is, returns the column in 'matching' list that have the same name than the 'column'.
 */
public class NamingColumnMatcher implements ColumnMatcher {

    public static final String NAME = "naming";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public FieldDescription getCanonicalFromColumn(String column, List<FieldDescription> canonicalColumns, List<FieldDescription> columns) {
        for (FieldDescription fieldDescription : canonicalColumns) {
            if (fieldDescription.getName().equals(column))
                return fieldDescription;

        }
        return null;
    }

    @Override
    public String getColumnFromCanonical(FieldDescription canonicalColumn, List<FieldDescription> canonicalColumns, List<FieldDescription> columns) {
        return canonicalColumns.contains(canonicalColumn) ? canonicalColumn.getName() : null;
    }


}
