package org.someth2say.taijitu.matcher;

import org.someth2say.taijitu.util.Named;

import java.util.List;

public interface ColumnMatcher extends Named {

    String getCanonicalFromColumn(final String column, final List<String> canonicalColumns, final List<String> columns);

    String getColumnFromCanonical(final String canonicalColumn, final List<String> canonicalColumns, final List<String> columns);
}
