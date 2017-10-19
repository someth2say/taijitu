package org.someth2say.taijitu.tuple;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.ComparisonRuntime;
import org.someth2say.taijitu.matcher.ColumnMatcher;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ResultSetTupleBuilder implements TupleBuilder<ResultSet, ComparableTuple> {
    private static final Logger logger = Logger.getLogger(ResultSetTupleBuilder.class);

    private final ColumnMatcher matcher;
    private final ComparisonRuntime runtime;
    private final String queryName;

    public ResultSetTupleBuilder(final ColumnMatcher matcher, final ComparisonRuntime runtime, String queryName) {
        this.matcher = matcher;
        this.runtime = runtime;
        this.queryName = queryName;
    }

    @Override
    public ComparableTuple apply(ResultSet resultSet) {
        List<String> canonicalColumns = runtime.getCanonicalColumns();
        List<String> providedColumns = runtime.getProvidedColumns(queryName);
        Object[] values = extract(matcher, resultSet, canonicalColumns, providedColumns);
        return new ComparableTuple(values, runtime);
    }

    private static Object[] extract(ColumnMatcher matcher, ResultSet rs, List<String> canonicalColumns, List<String> providedColumns) {
        Object[] columnValues = new Object[canonicalColumns.size()];
        int columnIdx = 0;
        for (String canonicalColumn : canonicalColumns) {
            String column = matcher.getColumnFromCanonical(canonicalColumn, canonicalColumns, providedColumns);
            try {
                columnValues[columnIdx++] = rs.getObject(column);
            } catch (SQLException e) {
                logger.error("Can\'t retrieve value for column" + column + "(canonical column was " + canonicalColumn + ")", e);
                return null;
            }
        }
        return columnValues;
    }
}
