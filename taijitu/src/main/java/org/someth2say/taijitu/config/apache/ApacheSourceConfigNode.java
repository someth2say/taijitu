package org.someth2say.taijitu.config.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.SourceConfig;

import java.util.List;

public class ApacheSourceConfigNode extends ApacheNamedConfigNode implements SourceConfig {
    protected ApacheSourceConfigNode(ImmutableHierarchicalConfiguration configuration) {
        super(configuration);
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public List<String> getKeyFields() {
        return null;
    }

}
