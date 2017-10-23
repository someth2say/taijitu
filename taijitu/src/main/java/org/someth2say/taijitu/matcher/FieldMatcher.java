package org.someth2say.taijitu.matcher;

import org.someth2say.taijitu.tuple.FieldDescription;
import org.someth2say.taijitu.util.Named;

import java.util.List;

public interface FieldMatcher extends Named {

    FieldDescription getCanonicalFromField(final String field, final List<FieldDescription> canonicalFields, final List<FieldDescription> fields);

    String getFieldFromCanonical(final FieldDescription canonicalField, final List<FieldDescription> canonicalFields, final List<FieldDescription> fields);
}
