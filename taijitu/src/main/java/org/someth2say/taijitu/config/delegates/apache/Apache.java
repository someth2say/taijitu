package org.someth2say.taijitu.config.delegates.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.delegates.apache.defaults.ApacheCfgDefaults;

public abstract class Apache<P extends ApacheCfgDefaults> implements ApacheCfgDefaults {
    private final ImmutableHierarchicalConfiguration configuration;


    Apache(final ImmutableHierarchicalConfiguration configuration) {
        this.configuration = configuration;
    }


    public ImmutableHierarchicalConfiguration getConfiguration() {
        return configuration;
    }

    //TODO: Woho! Apache Configuration does not defines equals/hashcode! :'(
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Apache)) return false;

        Apache<?> other = (Apache<?>) o;

        return getConfiguration().equals(other.getConfiguration());
    }

    @Override
    public int hashCode() {
        return getConfiguration().hashCode();
    }
}
