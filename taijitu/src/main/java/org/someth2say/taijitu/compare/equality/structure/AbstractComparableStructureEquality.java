package org.someth2say.taijitu.compare.equality.structure;

import org.someth2say.taijitu.compare.equality.value.ValueEquality;
import org.someth2say.taijitu.tuple.FieldDescription;

import java.util.Map;

public abstract class AbstractComparableStructureEquality<T> extends AbstractStructureEquality<T> implements ComparableStructureEquality<T> {
    public AbstractComparableStructureEquality(Map<FieldDescription, ValueEquality<?>> valueEqualitiesMap) {
        super(valueEqualitiesMap);
    }
}
