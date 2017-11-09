package org.someth2say.taijitu.tuple;

import org.someth2say.taijitu.compare.equality.structure.StructureEqualityWrapper;
import org.someth2say.taijitu.compare.equality.value.ValueEquality;

import java.util.List;
import java.util.Map;

public class TupleEquality extends AbstractTupleEquality {

    public TupleEquality(Map<FieldDescription, ? extends ValueEquality<?>> valueEqualitiesMap, List<FieldDescription> tupleFields) {
        super(valueEqualitiesMap, tupleFields);
    }

    @Override
    public StructureEqualityWrapper<Tuple> wrap(Tuple obj) {
        return new StructureEqualityWrapper<>(obj, this);
    }

}
