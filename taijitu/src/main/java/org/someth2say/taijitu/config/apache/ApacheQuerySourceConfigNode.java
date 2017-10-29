package org.someth2say.taijitu.config.apache;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.DatabaseConfig;
import org.someth2say.taijitu.config.QuerySourceConfig;

public class ApacheQuerySourceConfigNode extends ApacheSourceConfigNode implements QuerySourceConfig {
    protected ApacheQuerySourceConfigNode(ImmutableHierarchicalConfiguration configuration) {
        super(configuration);
    }

    @Override
    public String getStatement() {
        return null;
    }

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public Object[] getQueryParameters() {
        return new Object[0];
    }

    @Override
    public DatabaseConfig getDatabaseConfig() {
        return null;
    }
}
