package org.someth2say.taijitu.matcher;

import java.util.List;

/**
 * Matches columns by position. That is, returns the column in 'matching' in the same position than 'column' in 'columns' list.
 */
public class PositionalColumnMatcher implements ColumnMatcher {
    public static final String NAME = "position";

    @Override
    public String getName() {
        return this.NAME;
    }

    @Override
    public String getCanonicalFromColumn(String column, List<String> canonicalColumns, List<String> columns) {
        int index = columns.indexOf(column);
        if (index >= 0 && index < canonicalColumns.size()) {
            return canonicalColumns.get(index);
        }
        return null;
    }

    @Override
    public String getColumnFromCanonical(String canonicalColumn, List<String> canonicalColumns, List<String> columns) {
        int index = canonicalColumns.indexOf(canonicalColumn);
        if (index >= 0 && index < columns.size()) {
            return columns.get(index);
        }
        return null;
    }
}
