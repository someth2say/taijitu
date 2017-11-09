package org.someth2say.taijitu.source.csv;

import org.someth2say.taijitu.matcher.FieldMatcher;
import org.someth2say.taijitu.tuple.FieldDescription;
import org.someth2say.taijitu.tuple.TupleBuilder;

import java.util.List;

public abstract class AbstractTupleBuilder<T> implements TupleBuilder<T> {
    private final FieldMatcher matcher;
    // TODO: Ew...
    private List<FieldDescription> canonicalFields;

    public AbstractTupleBuilder(FieldMatcher matcher, List<FieldDescription> canonicalFields) {

        this.matcher = matcher;
        this.canonicalFields = canonicalFields;
    }

    public FieldMatcher getMatcher() {
        return matcher;
    }

    public List<FieldDescription> getCanonicalFields() {
        return canonicalFields;
    }

    public void setCanonicalFields(List<FieldDescription> canonicalFields) {
        this.canonicalFields = canonicalFields;
    }

}
