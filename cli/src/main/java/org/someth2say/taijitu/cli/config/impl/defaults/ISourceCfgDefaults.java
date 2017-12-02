package org.someth2say.taijitu.cli.config.impl.defaults;

import org.someth2say.taijitu.cli.config.interfaces.ISourceCfg;

import java.util.Properties;

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