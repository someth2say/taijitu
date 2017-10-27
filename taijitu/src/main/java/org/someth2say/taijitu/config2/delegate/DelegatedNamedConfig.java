package org.someth2say.taijitu.config2.delegate;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.util.Named;

public abstract class DelegatedNamedConfig extends ApacheDelegatedConfig implements Named{
    private final String name;

    public DelegatedNamedConfig(ImmutableHierarchicalConfiguration configuration) {
        super(configuration);
        this.name = getConfiguration().getRootElementName();
    }

    @Override
    public String getName() {
        return name;
    }
}
