package org.someth2say.taijitu.source.mapper;

import org.someth2say.taijitu.matcher.FieldMatcher;
import org.someth2say.taijitu.tuple.FieldDescription;
import org.someth2say.taijitu.tuple.TupleMapper;

import java.util.List;

public abstract class AbstractTupleMapper<T> implements TupleMapper<T> {
    private final FieldMatcher matcher;
    private final List<FieldDescription> canonicalFields;
    private final List<FieldDescription> providedFields;

    public AbstractTupleMapper(FieldMatcher matcher, List<FieldDescription> canonicalFields, List<FieldDescription> providedFields) {
        this.matcher = matcher;
        this.canonicalFields = canonicalFields;
        this.providedFields = providedFields;
    }

    public FieldMatcher getMatcher() {
        return matcher;
    }

    public List<FieldDescription> getCanonicalFields() {
        return canonicalFields;
    }

    public List<FieldDescription> getProvidedFields() {
        return providedFields;
    }
}
