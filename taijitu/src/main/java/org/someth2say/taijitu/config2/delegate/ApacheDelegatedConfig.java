package org.someth2say.taijitu.config2.delegate;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;

public abstract class ApacheDelegatedConfig {

	private final ImmutableHierarchicalConfiguration configuration;

	public ApacheDelegatedConfig(ImmutableHierarchicalConfiguration configuration) {

		this.configuration = configuration;
	}

	protected ImmutableHierarchicalConfiguration getConfiguration(){
		return this.configuration;
	}
}
