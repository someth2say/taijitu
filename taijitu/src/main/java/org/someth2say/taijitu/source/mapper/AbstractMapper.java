package org.someth2say.taijitu.source.mapper;

import java.util.List;

import org.someth2say.taijitu.matcher.FieldMatcher;
import org.someth2say.taijitu.tuple.FieldDescription;

public abstract class AbstractMapper<T,R> implements Mapper<T, R> {

	private final FieldMatcher matcher;
	private final List<FieldDescription> canonicalFields;
	private final List<FieldDescription> providedFields;

	public AbstractMapper(FieldMatcher matcher, List<FieldDescription> canonicalFields,
			List<FieldDescription> providedFields) {
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
