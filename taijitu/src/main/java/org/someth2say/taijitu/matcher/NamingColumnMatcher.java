package org.someth2say.taijitu.matcher;

import java.util.List;

public class NamingColumnMatcher implements ColumnMatcher {

    public static final String NAME = "naming";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getMatchingColumn(String column, List<String> matching, List<String> columns) {
        return matching.contains(column) ? column : null;
    }


}
