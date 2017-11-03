package org.someth2say.taijitu.source.csv;

import java.util.List;

import org.someth2say.taijitu.ComparisonContext;
import org.someth2say.taijitu.matcher.FieldMatcher;
import org.someth2say.taijitu.tuple.ComparableTuple;
import org.someth2say.taijitu.tuple.FieldDescription;
import org.someth2say.taijitu.tuple.TupleBuilder;

public class CSVTupleBuilder implements TupleBuilder<String> {

    private final FieldMatcher matcher;
    private final ComparisonContext context;
    private final String sourceId;

    public CSVTupleBuilder(final FieldMatcher matcher, final ComparisonContext context, final String sourceId) {
        this.matcher = matcher;
        this.context = context;
        this.sourceId = sourceId;
    }

    @Override
    public ComparableTuple apply(String line) {
        List<FieldDescription> canonicalFields = context.getCanonicalFields();
        List<FieldDescription> providedFields = context.getProvidedFields(sourceId);
        Object[] values = extract(matcher, line, canonicalFields, providedFields);
        return new ComparableTuple(values, context);
    }

    private static Object[] extract(FieldMatcher matcher, String line, List<FieldDescription> canonicalFields,
                                    List<FieldDescription> providedFields) {
        Object[] fieldValues = new Object[canonicalFields.size()];
        String[] providedValues = line.split(",");
        int canonicalFieldIdx = 0;
        for (FieldDescription canonicalField : canonicalFields) {
            FieldDescription providedField = matcher.getFieldFromCanonical(canonicalField, canonicalFields,
                    providedFields);
            int providedFieldPos = providedFields.indexOf(providedField);
            String providedValue = providedValues[providedFieldPos];
            fieldValues[canonicalFieldIdx++] = providedValue != null ? providedValue.trim() : null;
        }
        return fieldValues;
    }

}
