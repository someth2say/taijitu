package org.someth2say.taijitu.tuple.builder;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.ComparisonContext;
import org.someth2say.taijitu.matcher.FieldMatcher;
import org.someth2say.taijitu.tuple.ComparableTuple;
import org.someth2say.taijitu.tuple.FieldDescription;
import org.someth2say.taijitu.tuple.TupleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ResultSetTupleBuilder implements TupleBuilder<ResultSet> {
    private static final Logger logger = Logger.getLogger(ResultSetTupleBuilder.class);

    private final FieldMatcher matcher;
    private final ComparisonContext context;
    private final String sourceId;

    public ResultSetTupleBuilder(final FieldMatcher matcher, final ComparisonContext context, final String sourceId) {
        this.matcher = matcher;
        this.context = context;
        this.sourceId = sourceId;
    }

    @Override
    public ComparableTuple apply(ResultSet resultSet) {
        List<FieldDescription> canonicalFields = context.getCanonicalFields();
        List<FieldDescription> providedFields = context.getProvidedFields(sourceId);
        Object[] values = extract(matcher, resultSet, canonicalFields, providedFields);
        return new ComparableTuple(values, context);
    }

    private static Object[] extract(FieldMatcher matcher, ResultSet rs, List<FieldDescription> canonicalFields, List<FieldDescription> providedFields) {
        //TODO: Worth having a pool of objects, that can be reused (even a pool of Tuple objects, if external comparison in place)
        Object[] fieldValues = new Object[canonicalFields.size()];
        int fieldIdx = 0;
        for (FieldDescription canonicalField : canonicalFields) {
            FieldDescription field = matcher.getFieldFromCanonical(canonicalField, canonicalFields, providedFields);
            try {
                //TODO: check if rs.getObject(position) is faster, including field position in FieldDescription
                fieldValues[fieldIdx++] = rs.getObject(field.getName());
            } catch (SQLException e) {
                logger.error("Can\'t retrieve value for field" + field + "(canonical field was " + canonicalField + ")", e);
                return null;
            }
        }
        return fieldValues;
    }

}
