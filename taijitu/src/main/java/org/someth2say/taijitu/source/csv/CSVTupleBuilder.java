package org.someth2say.taijitu.source.csv;

import java.util.List;

import org.someth2say.taijitu.matcher.FieldMatcher;
import org.someth2say.taijitu.tuple.FieldDescription;
import org.someth2say.taijitu.tuple.Tuple;
import org.someth2say.taijitu.tuple.TupleBuilder;

public class CSVTupleBuilder extends AbstractTupleBuilder<String> {

    public CSVTupleBuilder(final FieldMatcher matcher, List<FieldDescription> canonicalFields) {
        super(matcher,canonicalFields);
    }

    @Override
    public Tuple apply(String line, List<FieldDescription> providedFields) {
        Object[] values = extract(getMatcher(), line, getCanonicalFields(), providedFields);
        return new Tuple(values);
    }

    private static Object[] extract(FieldMatcher matcher, String line, List<FieldDescription> canonicalFields,
                                    List<FieldDescription> providedFields) {
        Object[] fieldValues = new Object[canonicalFields.size()];
        String[] providedValues = line.split(",");
        int canonicalFieldIdx = 0;
        for (FieldDescription canonicalField : canonicalFields) {
            FieldDescription providedField = matcher.getProvidedField(canonicalField, canonicalFields,
                    providedFields);
            int providedFieldPos = providedFields.indexOf(providedField);
            String providedValue = providedValues[providedFieldPos];
            fieldValues[canonicalFieldIdx++] = providedValue != null ? providedValue.trim() : null;
        }
        return fieldValues;
    }

}
