package org.someth2say.taijitu.config.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.ConfigurationLabels;
import org.someth2say.taijitu.config.SourceConfig;

import java.util.List;

public abstract class ApacheSourceConfigNode extends ApacheNamedConfigNode implements SourceConfig {

    ApacheSourceConfigNode(ImmutableHierarchicalConfiguration configuration, ApacheSourceConfigNode parent) {
        super(configuration, parent);
    }

    @Override
    public String getType() {
        return getConfiguration().getString(ConfigurationLabels.Comparison.SOURCE_TYPE);
    }

    @Override
    public List<String> getKeyFields() {
        //TODO: Consider '*' wildcard for key fields
        return getConfiguration().getList(String.class, ConfigurationLabels.Comparison.Fields.KEYS, null);
    }

}
