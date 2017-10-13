package org.someth2say.taijitu.matcher;

import java.util.List;

/**
 * Returns the very same column. Assumes 'canonicalColumns' and 'columns' are always the same.
 */

public class IdentityColumnMatcher implements ColumnMatcher {

    public static final String NAME = "identity";

    @Override
    public String getCanonicalFromColumn(final String column, final List<String> canonicalColumns, final List<String> columns) {
        return column;
    }

    @Override
    public String getColumnFromCanonical(final String canonicalColumn, final List<String> canonicalColumns, final List<String> columns) {
        return canonicalColumn;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
