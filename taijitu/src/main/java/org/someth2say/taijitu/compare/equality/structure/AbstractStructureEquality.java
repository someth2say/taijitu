package org.someth2say.taijitu.compare.equality.structure;

import org.someth2say.taijitu.compare.equality.value.ValueEquality;
import org.someth2say.taijitu.tuple.FieldDescription;

import java.util.Map;

public abstract class AbstractStructureEquality<T> implements StructureEquality<T> {

    private final Map<FieldDescription, ? extends ValueEquality<?>> valueEqualitiesMap;
    public AbstractStructureEquality(Map<FieldDescription, ? extends ValueEquality<?>> valueEqualitiesMap) {
        this.valueEqualitiesMap = valueEqualitiesMap;
    }

    public Map<FieldDescription, ? extends ValueEquality<?>> getValueEqualitiesMap() {
        return valueEqualitiesMap;
    }
}
