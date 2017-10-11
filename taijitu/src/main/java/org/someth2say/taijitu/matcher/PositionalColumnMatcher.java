package org.someth2say.taijitu.matcher;

import java.util.List;

public class PositionalColumnMatcher implements ColumnMatcher {
    public static final String NAME = "position";

    @Override
    public String getName() {
        return this.NAME;
    }

    @Override
    public String getMatchingColumn(String column, List<String> matching, List<String> columns) {
        int index = columns.indexOf(column);
        if (index >= 0 && index < columns.size()) {
            return matching.get(index);
        }
        return null;
    }
}
