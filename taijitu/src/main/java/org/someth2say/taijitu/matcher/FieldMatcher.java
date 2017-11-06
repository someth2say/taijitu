package org.someth2say.taijitu.matcher;

import org.someth2say.taijitu.tuple.FieldDescription;
import org.someth2say.taijitu.util.Named;

import java.util.List;

public interface FieldMatcher extends Named {

    FieldDescription getCanonicalField(final FieldDescription providedField, final List<FieldDescription> canonicalFields, final List<FieldDescription> providedFields);

    FieldDescription getProvidedField(FieldDescription canonicalField, List<FieldDescription> canonicalFields, List<FieldDescription> providedFields);
}
