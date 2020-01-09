package org.someth2say.taijitu.cli.config.impl;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.someth2say.taijitu.cli.config.interfaces.ICfg;

public abstract class Cfg implements ICfg {
    private final HierarchicalConfiguration configuration;


    Cfg(final HierarchicalConfiguration configuration) {
        this.configuration = configuration;
    }


    @Override
	public HierarchicalConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cfg)) return false;
        Cfg other = (Cfg) o;
        //Cfg Configuration does not define areEquals/hashcode! :'(
        return getConfiguration().equals(other.getConfiguration());
    }

    @Override
    public int hashCode() {
        return getConfiguration().hashCode();
    }
}
