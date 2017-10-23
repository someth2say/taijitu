package org.someth2say.taijitu.matcher;

import org.someth2say.taijitu.tuple.FieldDescription;

import java.util.List;

/**
 * Returns the very same column. Assumes 'canonicalColumns' and 'columns' are always the same.
 */

public class IdentityColumnMatcher implements ColumnMatcher {

    public static final String NAME = "identity";

    @Override
    public FieldDescription getCanonicalFromColumn(final String column, final List<FieldDescription> canonicalColumns, final List<FieldDescription> columns) {
        return canonicalColumns.stream().filter(fd -> fd.getName().equals(column)).findFirst().get();
    }

    @Override
    public String getColumnFromCanonical(final FieldDescription canonicalColumn, final List<FieldDescription> canonicalColumns, final List<FieldDescription> columns) {
        return canonicalColumn.getName();
    }

    @Override
    public String getName() {
        return NAME;
    }
}
