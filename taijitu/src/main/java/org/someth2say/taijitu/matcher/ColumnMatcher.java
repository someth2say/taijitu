package org.someth2say.taijitu.matcher;

import org.someth2say.taijitu.util.Named;

import java.util.List;

public interface ColumnMatcher extends Named {
    String getMatchingColumn(final String column, final List<String> matching, final List<String> columns);

    String getReverseMatchingColumn(String canonicalColumn, final List<String> matching, final List<String> columns);
}
