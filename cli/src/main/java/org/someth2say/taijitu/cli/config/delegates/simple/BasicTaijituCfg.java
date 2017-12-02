package org.someth2say.taijitu.cli.config.delegates.simple;

import org.someth2say.taijitu.cli.config.interfaces.*;

import java.util.List;
import java.util.Properties;

public class BasicTaijituCfg implements ITaijituCfg {

    private final String name;
    private List<IComparisonCfg> comparisons;
    private Integer threads;

    private Boolean isUseScanClassPath;
    private IStrategyCfg strategyConfig;

    private List<IEqualityCfg> equalityConfigs;
    private List<ISourceCfg> sourceConfigs;
    private List<IPluginCfg> comparisonPluginConfigs;

    //TODO: Maybe we should only allow to default the whole equalityCfg, not every field....
    private String fieldName;
    private String fieldClass;
    private Boolean isFieldClassStrict;
    private Object equalityParameters;

    //TODO: Same for sourceCfg...
    private String type;
    private List<String> keyFields;
    private List<String> sortFields;
    private List<String> compareFields;
    private Properties buildProperties;
    private Properties fetchProperties;
    private String mapper;

    public void setComparisons(List<IComparisonCfg> comparisons) {
        this.comparisons = comparisons;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public void setUseScanClassPath(Boolean useScanClassPath) {
        isUseScanClassPath = useScanClassPath;
    }

    public void setStrategyConfig(IStrategyCfg strategyConfig) {
        this.strategyConfig = strategyConfig;
    }

    public void setEqualityConfigs(List<IEqualityCfg> equalityConfigs) {
        this.equalityConfigs = equalityConfigs;
    }

    public void setBuildProperties(Properties buildProperties) {
        this.buildProperties = buildProperties;
    }

    public void setSourceConfigs(List<ISourceCfg> sourceConfigs) {
        this.sourceConfigs = sourceConfigs;
    }

    public void setComparisonPluginConfigs(List<IPluginCfg> comparisonPluginConfigs) {
        this.comparisonPluginConfigs = comparisonPluginConfigs;
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

    public void setSortFields(List<String> sortFields) {
        this.sortFields = sortFields;
    }

    public void setCompareFields(List<String> compareFields) {
        this.compareFields = compareFields;
    }

    public void setFetchProperties(Properties fetchProperties) {
        this.fetchProperties = fetchProperties;
    }

    public BasicTaijituCfg(String name) {
        this.name = name;
    }

    public void setMapper(String mapper) {
        this.mapper = mapper;
    }


    @Override
    public List<IComparisonCfg> getComparisons() {
        return this.comparisons;
    }

    @Override
    public Integer getThreads() {
        return this.threads;
    }


    @Override
    public Boolean isUseScanClassPath() {
        return this.isUseScanClassPath;
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
    public List<String> getSortFields() {
        return this.sortFields;
    }

    @Override
    public List<String> getCompareFields() {
        return this.compareFields;
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
    public String getName() {
        return this.name;
    }

    @Override
    public String getMapper() {
        return mapper;
    }
}
