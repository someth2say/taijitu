package org.someth2say.taijitu.compare.equality.tuple;

import org.someth2say.taijitu.compare.equality.value.SortedValueEquality;
import org.someth2say.taijitu.compare.equality.value.ValueEquality;
import org.someth2say.taijitu.tuple.FieldDescription;

import java.util.List;
import java.util.Map;

public abstract class AbstractSortedStructureEquality<T> extends AbstractStructureEquality<T> implements SortedStructureEquality<T> {
    public AbstractSortedStructureEquality(Map<FieldDescription, ValueEquality<?>> valueEqualitiesMap) {
        super(valueEqualitiesMap);
    }
}
