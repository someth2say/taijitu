package org.someth2say.taijitu.config.delegates.simple;

import org.someth2say.taijitu.config.interfaces.ISourceCfg;

import java.util.List;
import java.util.Properties;

public class BasicSourceCfg implements ISourceCfg {

    private final String name;
    private String type;
    private List<String> keyFields;
    private Properties fetchProperties;
    private Properties buildProperties;

    public BasicSourceCfg(String name, String type, Properties fetchProperties, Properties buildProperties) {
        this.type = type;
        this.name = name;
        this.buildProperties = buildProperties;
        this.fetchProperties = fetchProperties;
    }

    public String getType() {
        return this.type;
    }

    public List<String> getKeyFields() {
        return this.keyFields;
    }

    public Properties getFetchProperties() {
        return this.fetchProperties;
    }

    public Properties getBuildProperties() {
        return this.buildProperties;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setKeyFields(List<String> keyFields) {
        this.keyFields = keyFields;
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
}
