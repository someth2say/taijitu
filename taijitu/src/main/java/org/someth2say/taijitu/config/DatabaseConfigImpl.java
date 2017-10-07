package org.someth2say.taijitu.config;

import java.util.Properties;

public class DatabaseConfigImpl implements DatabaseConfig {

	private TaijituConfig taijituConfig;
	private final String name;

    public DatabaseConfigImpl(final TaijituConfig taijituConfig, final String name){
		this.taijituConfig = taijituConfig;
		this.name = name;
    }

	@Override
	public Properties getAsProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		return name;
	}

}
