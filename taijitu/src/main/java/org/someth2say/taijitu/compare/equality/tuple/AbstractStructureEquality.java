package org.someth2say.taijitu.compare.equality.tuple;

import org.someth2say.taijitu.compare.equality.value.ValueEquality;
import org.someth2say.taijitu.tuple.FieldDescription;

import java.util.Map;

public abstract class AbstractStructureEquality<T> implements StructureEquality<T> {

    private final Map<FieldDescription, ValueEquality<?>> valueEqualitiesMap;
    public AbstractStructureEquality(Map<FieldDescription, ValueEquality<?>> valueEqualitiesMap) {
        this.valueEqualitiesMap = valueEqualitiesMap;
    }

    abstract public Object extractFieldValue(T tuple, FieldDescription fieldDescription);

    public Map<FieldDescription, ValueEquality<?>> getValueEqualitiesMap() {
        return valueEqualitiesMap;
    }
}
