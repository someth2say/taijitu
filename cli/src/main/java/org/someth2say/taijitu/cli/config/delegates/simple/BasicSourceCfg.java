package org.someth2say.taijitu.cli.config.delegates.simple;

import org.someth2say.taijitu.cli.config.interfaces.ISourceCfg;

import java.util.Properties;

public class BasicSourceCfg implements ISourceCfg {

    private final String name;
    private String type;
    private Properties fetchProperties;
    private Properties buildProperties;
    private String mapper;

    public BasicSourceCfg(String name, String type, Properties fetchProperties, Properties buildProperties, String mapper) {
        this.type = type;
        this.name = name;
        this.buildProperties = buildProperties;
        this.fetchProperties = fetchProperties;
        this.mapper = mapper;
    }

    @Override
	public String getType() {
        return this.type;
    }


    @Override
	public Properties getFetchProperties() {
        return this.fetchProperties;
    }

    @Override
	public Properties getBuildProperties() {
        return this.buildProperties;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setFetchProperties(Properties fetchProperties) {
        this.fetchProperties = fetchProperties;
    }

    public void setBuildProperties(Properties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @Override
    public String getName() {
        return name;
    }

	@Override
	public String getMapper() {
		return this.mapper;
	}

	public void setMapper(String mapper) {
		this.mapper = mapper;
	}
}