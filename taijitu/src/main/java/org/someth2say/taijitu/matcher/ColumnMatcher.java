package org.someth2say.taijitu.matcher;

import org.someth2say.taijitu.tuple.FieldDescription;
import org.someth2say.taijitu.util.Named;

import java.util.List;

public interface ColumnMatcher extends Named {

    FieldDescription getCanonicalFromColumn(final String column, final List<FieldDescription> canonicalColumns, final List<FieldDescription> columns);

    String getColumnFromCanonical(final FieldDescription canonicalColumn, final List<FieldDescription> canonicalColumns, final List<FieldDescription> columns);
}
