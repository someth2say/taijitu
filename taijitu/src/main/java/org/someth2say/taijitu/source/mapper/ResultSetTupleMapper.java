package org.someth2say.taijitu.source.mapper;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.matcher.FieldMatcher;
import org.someth2say.taijitu.tuple.FieldDescription;
import org.someth2say.taijitu.tuple.Tuple;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ResultSetTupleMapper extends AbstractTupleMapper<ResultSet> {
    private static final Logger logger = Logger.getLogger(ResultSetTupleMapper.class);

    public ResultSetTupleMapper(FieldMatcher matcher, List<FieldDescription> canonicalFields, List<FieldDescription> providedFields) {
        super(matcher, canonicalFields, providedFields);
    }

    @Override
    public Tuple apply(ResultSet rs) {
        Object[] fieldValues = new Object[getCanonicalFields().size()];
        int fieldIdx = 0;
        for (FieldDescription canonicalField : getCanonicalFields()) {
            FieldDescription providedField = getMatcher().getProvidedField(canonicalField, getCanonicalFields(), getProvidedFields());
            try {
                //TODO: check if rs.getObject(position) is faster, including providedField position in FieldDescription
                fieldValues[fieldIdx++] = rs.getObject(providedField.getName());
            } catch (SQLException e) {
                logger.error("Can\'t retrieve value for field" + providedField + "(canonical was " + canonicalField + ")", e);
            }
        }
        return new Tuple(fieldValues);
    }
}
