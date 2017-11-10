package org.someth2say.taijitu.source.mapper;

import java.util.List;

import org.someth2say.taijitu.matcher.FieldMatcher;
import org.someth2say.taijitu.tuple.FieldDescription;
import org.someth2say.taijitu.tuple.Tuple;

/**
 * Mapper that transforms a CSVSource (an Object[]) to a Tuple (also and Object[]).
 * The point here is that NOT ALL FIELDS are transformed, nor in the same order.
 * Mapping depends on the description for the CSVSource (providedFields) and the Tuple (canonicalFields);
 */
public class CSVTupleMapper extends AbstractTupleMapper<Object[]> {

    public CSVTupleMapper(FieldMatcher matcher, List<FieldDescription> canonicalFields, List<FieldDescription> providedFields) {
        super(matcher, canonicalFields, providedFields);
    }

    @Override
    public Tuple apply(Object[] csvEntry) {
        FieldMatcher matcher = getMatcher();
        List<FieldDescription> tupleFields = getCanonicalFields();
        List<FieldDescription> csvFields = getProvidedFields();
        Object[] tupleValues = new Object[tupleFields.size()];
        int canonicalFieldIdx = 0;
        for (FieldDescription tupleField : tupleFields) {
            FieldDescription csvField = matcher.getProvidedField(tupleField, tupleFields, csvFields);
            int csvFieldIdx = csvFields.indexOf(csvField);
            tupleValues[canonicalFieldIdx++] = csvEntry[csvFieldIdx];
        }
        return new Tuple(tupleValues);
    }


}
