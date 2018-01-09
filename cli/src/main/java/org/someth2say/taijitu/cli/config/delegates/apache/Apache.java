package org.someth2say.taijitu.cli.config.delegates.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.cli.config.delegates.apache.defaults.ApacheCfgDefaults;

public abstract class Apache implements ApacheCfgDefaults {
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
        Apache other = (Apache) o;
        //Apache Configuration does not defines areEquals/hashcode! :'(
        return getConfiguration().equals(other.getConfiguration());
    }

    @Override
    public int hashCode() {
        return getConfiguration().hashCode();
    }
}
