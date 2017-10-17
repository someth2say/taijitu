package org.someth2say.taijitu.config.old;

import org.someth2say.taijitu.config.DatabaseConfig;

import java.util.Properties;

@Deprecated
public class DatabaseConfigImpl implements DatabaseConfig {

    private TaijituConfigImpl taijituConfig;
    private final String name;

    public DatabaseConfigImpl(final TaijituConfigImpl taijituConfig, final String name) {
        this.taijituConfig = taijituConfig;
        this.name = name;
    }

    @Override
    public Properties getAsProperties() {
        return taijituConfig.getConfig().getSubPropertiesByPrefix("database." + name).getDelegate();
    }

    public String getName() {
        return name;
    }

}
