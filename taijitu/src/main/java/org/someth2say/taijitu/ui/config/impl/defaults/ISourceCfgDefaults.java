package org.someth2say.taijitu.ui.config.impl.defaults;

import java.util.List;
import java.util.Properties;

import org.someth2say.taijitu.ui.config.DefaultConfig;
import org.someth2say.taijitu.ui.config.interfaces.ISourceCfg;

public interface ISourceCfgDefaults<T extends ISourceCfg> extends ISourceCfg, ICfgDefaults<T> {
	@Override
	default String getMapper() {
		String mapper = getDelegate().getMapper();
		return mapper != null ? mapper : getParent() != null ? getParent().getMapper() : null;
	}

	@Override
	default String getType() {
		return getDelegate().getType();
	}

	@Override
	default List<String> getKeyFields() {
		List<String> keyFields = getDelegate().getKeyFields();
		return keyFields != null ? keyFields
				: getParent() != null ? getParent().getKeyFields() : DefaultConfig.DEFAULT_KEY_FIELDS;
	}

	@Override
	default Properties getFetchProperties() {
		Properties sourceProperties = getDelegate().getFetchProperties();
		return sourceProperties != null ? sourceProperties
				: getParent() != null ? getParent().getFetchProperties() : null;
	}

	@Override
	default Properties getBuildProperties() {
		Properties sourceProperties = getDelegate().getBuildProperties();
		return sourceProperties != null ? sourceProperties
				: getParent() != null ? getParent().getBuildProperties() : null;
	}

}
