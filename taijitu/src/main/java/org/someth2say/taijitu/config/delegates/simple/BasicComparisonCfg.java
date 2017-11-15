package org.someth2say.taijitu.config.delegates.simple;

import org.someth2say.taijitu.config.interfaces.*;

import java.util.List;
import java.util.Properties;

public class BasicComparisonCfg implements IComparisonCfg {

    private IStrategyCfg strategyConfig;

    private List<IEqualityCfg> equalityConfigs;
    private List<ISourceCfg> sourceConfigs;
    private List<IPluginCfg> comparisonPluginConfigs;

    private final String name;

    //TODO: Maybe we should only allow to default the whole equalityCfg, not every field....
    private String fieldName;
    private String fieldClass;
    private Boolean isFieldClassStrict;
    private Object equalityParameters;

    //TODO: Same for sourceCfg...
    private String type;
    private List<String> keyFields;
    private Properties buildProperties;
    private Properties fetchProperties;
    private String mapper;

    public BasicComparisonCfg(String name) {
        this.name = name;
    }

    public BasicComparisonCfg(String name, List<String> keyFields, List<ISourceCfg> sourceConfigs) {
        this.name = name;
        this.keyFields = keyFields;
        this.sourceConfigs = sourceConfigs;
    }

    @Override
    public String getFieldName() {
        return this.fieldName;
    }

    @Override
    public String getFieldClass() {
        return this.fieldClass;
    }

    @Override
    public Boolean isFieldClassStrict() {
        return this.isFieldClassStrict;
    }

    @Override
    public Object getEqualityParameters() {
        return this.equalityParameters;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public List<String> getKeyFields() {
        return this.keyFields;
    }

    @Override
    public Properties getFetchProperties() {
        return this.fetchProperties;
    }

    @Override
    public Properties getBuildProperties() {
        return this.buildProperties;
    }

    @Override
    public IStrategyCfg getStrategyConfig() {
        return this.strategyConfig;
    }

    @Override
    public List<IEqualityCfg> getEqualityConfigs() {
        return this.equalityConfigs;
    }

    @Override
    public List<ISourceCfg> getSourceConfigs() {
        return this.sourceConfigs;
    }

    @Override
    public List<IPluginCfg> getPluginConfigs() {
        return this.comparisonPluginConfigs;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
	public String getMapper() {
		return mapper;
	}

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setFieldClass(String fieldClass) {
        this.fieldClass = fieldClass;
    }

    public void setFieldClassStrict(Boolean fieldClassStrict) {
        isFieldClassStrict = fieldClassStrict;
    }

    public void setEqualityParameters(Object equalityParameters) {
        this.equalityParameters = equalityParameters;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setKeyFields(List<String> keyFields) {
        this.keyFields = keyFields;
    }

    public void setBuildProperties(Properties buildProperties) {
        this.buildProperties = buildProperties;
    }

    public void setFetchProperties(Properties fetchProperties) {
        this.fetchProperties = fetchProperties;
    }

    public void setStrategyConfig(IStrategyCfg strategyConfig) {
        this.strategyConfig = strategyConfig;
    }

    public void setEqualityConfigs(List<IEqualityCfg> equalityConfigs) {
        this.equalityConfigs = equalityConfigs;
    }

    public void setSourceConfigs(List<ISourceCfg> sourceConfigs) {
        this.sourceConfigs = sourceConfigs;
    }

    public void setComparisonPluginConfigs(List<IPluginCfg> comparisonPluginConfigs) {
        this.comparisonPluginConfigs = comparisonPluginConfigs;
    }

	public void setMapper(String mapper) {
		this.mapper = mapper;
	}
}
