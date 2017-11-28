package org.someth2say.taijitu.ui.config.delegates.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.ui.config.delegates.apache.defaults.ApacheCfgDefaults;

public abstract class Apache<P extends ApacheCfgDefaults> implements ApacheCfgDefaults {
    private final ImmutableHierarchicalConfiguration configuration;


    Apache(final ImmutableHierarchicalConfiguration configuration) {
        this.configuration = configuration;
    }


    @Override
	public ImmutableHierarchicalConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Apache)) return false;
        Apache<?> other = (Apache<?>) o;
        //TODO: Woho! Apache Configuration does not defines equals/hashcode! :'(
        return getConfiguration().equals(other.getConfiguration());
    }

    @Override
    public int hashCode() {
        return getConfiguration().hashCode();
    }
}
