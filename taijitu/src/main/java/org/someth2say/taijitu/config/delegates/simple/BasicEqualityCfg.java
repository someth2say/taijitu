package org.someth2say.taijitu.config.delegates.simple;

import org.someth2say.taijitu.config.DefaultConfig;
import org.someth2say.taijitu.config.interfaces.IEqualityCfg;

public class BasicEqualityCfg implements IEqualityCfg {

    private Object equalityParameters;
    private Boolean fieldClassStrict = DefaultConfig.DEFAULT_FIELD_CLASS_STRICT;
    private String fieldClass;
    private String fieldName;
    private final String name;

    public BasicEqualityCfg(String name, String fieldClass, String fieldName) {
        this.name = name;
        this.fieldClass = fieldClass;
        this.fieldName = fieldName;
    }

    public BasicEqualityCfg(String name, String fieldClass, String fieldName, Object equalityParameters) {
        this.fieldClass = fieldClass;
        this.fieldName = fieldName;
        this.equalityParameters = equalityParameters;
        this.name = name;
    }

    public void setEqualityParameters(Object equalityParameters) {
        this.equalityParameters = equalityParameters;
    }

    public void setFieldClassStrict(Boolean fieldClassStrict) {
        this.fieldClassStrict = fieldClassStrict;
    }

    public void setFieldClass(String fieldClass) {
        this.fieldClass = fieldClass;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
    
    @Override
    public String getFieldName() {
        return this.fieldName;
    }

    @Override
    public String getFieldClass() {
        return fieldClass;
    }

    @Override
    public Boolean isFieldClassStrict() {
        return this.fieldClassStrict;
    }

    @Override
    public Object getEqualityParameters() {
        return equalityParameters;
    }


    @Override
    public String getName() {
        return this.name;
    }
}
