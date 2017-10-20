package org.someth2say.taijitu.config.impl;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.NamedConfig;
import org.someth2say.taijitu.config.QueryConfig;
import org.someth2say.taijitu.config.impl.apache.ApacheBasedQueryConfig;

public class QueryConfigImpl extends NamedConfig implements ApacheBasedQueryConfig {

    private final ImmutableHierarchicalConfiguration configuration;
    private final QueryConfig parent;

    public QueryConfigImpl(final ImmutableHierarchicalConfiguration configuration,
                           final QueryConfig parent) {
        super(configuration.getRootElementName());
        this.configuration = configuration;
        this.parent = parent;
    }

    public ImmutableHierarchicalConfiguration getConfiguration() {
        return configuration;
    }

    public QueryConfig getParent() {
        return parent;
    }

}
