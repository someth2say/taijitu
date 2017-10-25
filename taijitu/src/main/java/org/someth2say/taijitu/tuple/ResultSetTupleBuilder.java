package org.someth2say.taijitu.tuple;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.ComparisonRuntime;
import org.someth2say.taijitu.config.QueryConfig;
import org.someth2say.taijitu.matcher.FieldMatcher;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ResultSetTupleBuilder implements TupleBuilder<ResultSet, ComparableTuple> {
    private static final Logger logger = Logger.getLogger(ResultSetTupleBuilder.class);

    private final FieldMatcher matcher;
    private final ComparisonRuntime runtime;
    private final QueryConfig queryConfig;

    public ResultSetTupleBuilder(final FieldMatcher matcher, final ComparisonRuntime runtime, final QueryConfig queryConfig) {
        this.matcher = matcher;
        this.runtime = runtime;
        this.queryConfig = queryConfig;
    }

    @Override
    public ComparableTuple apply(ResultSet resultSet) {
        List<FieldDescription> canonicalFields = runtime.getCanonicalFields();
        List<FieldDescription> providedFields = runtime.getProvidedFields(queryConfig.getName());
        Object[] values = extract(matcher, resultSet, canonicalFields, providedFields);
        //logger.info("Built tuple: "+StringUtils.join(values,","));
        return new ComparableTuple(values, runtime);
    }

    private static Object[] extract(FieldMatcher matcher, ResultSet rs, List<FieldDescription> canonicalFields, List<FieldDescription> providedFields) {
        Object[] fieldValues = new Object[canonicalFields.size()];
        int fieldIdx = 0;
        for (FieldDescription canonicalField : canonicalFields) {
            String field = matcher.getFieldFromCanonical(canonicalField, canonicalFields, providedFields);
            try {
                fieldValues[fieldIdx++] = rs.getObject(field);
            } catch (SQLException e) {
                logger.error("Can\'t retrieve value for field" + field + "(canonical field was " + canonicalField + ")", e);
                return null;
            }
        }
        return fieldValues;
    }

}
