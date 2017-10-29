package org.someth2say.taijitu.config.node;

import org.someth2say.taijitu.config.DatabaseConfig;

public interface QuerySourceConfigNode extends SourceConfigNode {
    String getStatement();

    int getFetchSize();

    Object[] getQueryParameters();

    DatabaseConfig getDatabaseConfig();
}
