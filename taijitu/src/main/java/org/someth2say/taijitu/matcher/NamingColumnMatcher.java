package org.someth2say.taijitu.matcher;

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
    public String getCanonicalFromColumn(String column, List<String> canonicalColumns, List<String> columns) {
        return canonicalColumns.contains(column) ? column : null;
    }

    @Override
    public String getColumnFromCanonical(String canonicalColumn, List<String> canonicalColumns, List<String> columns) {
        return columns.contains(canonicalColumn) ? canonicalColumn : null;
    }


}
