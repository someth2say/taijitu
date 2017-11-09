package org.someth2say.taijitu.source.query;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.matcher.FieldMatcher;
import org.someth2say.taijitu.source.csv.AbstractTupleBuilder;
import org.someth2say.taijitu.tuple.FieldDescription;
import org.someth2say.taijitu.tuple.Tuple;
import org.someth2say.taijitu.tuple.TupleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ResultSetTupleBuilder extends AbstractTupleBuilder<ResultSet> {
    private static final Logger logger = Logger.getLogger(ResultSetTupleBuilder.class);

    public ResultSetTupleBuilder(FieldMatcher matcher, List<FieldDescription> canonicalFields) {
        super(matcher, canonicalFields);
    }


    @Override
    public Tuple apply(ResultSet resultSet,  List<FieldDescription> providedFields) {
        Object[] values = extract(getMatcher(), resultSet, getCanonicalFields(), providedFields);
        return new Tuple(values);
    }

    private static Object[] extract(FieldMatcher matcher, ResultSet rs, List<FieldDescription> canonicalFields, List<FieldDescription> providedFields) {
        Object[] fieldValues = new Object[canonicalFields.size()];
        int fieldIdx = 0;
        for (FieldDescription canonicalField : canonicalFields) {
            FieldDescription providedField = matcher.getProvidedField(canonicalField, canonicalFields, providedFields);
            try {
                //TODO: check if rs.getObject(position) is faster, including providedField position in FieldDescription
                fieldValues[fieldIdx++] = rs.getObject(providedField.getName());
            } catch (SQLException e) {
                logger.error("Can\'t retrieve value for field" + providedField + "(canonical was " + canonicalField + ")", e);
                return null;
            }
        }
        return fieldValues;
    }

}
