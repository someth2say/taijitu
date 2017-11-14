package org.someth2say.taijitu.tuple;

import org.someth2say.taijitu.compare.equality.composite.CompositeEqualityWrapper;
import org.someth2say.taijitu.compare.equality.value.ValueEquality;
import org.someth2say.taijitu.source.FieldDescription;

import java.util.List;
import java.util.Map;

@Deprecated
public class TupleEquality extends AbstractTupleEquality {

    public TupleEquality(Map<FieldDescription, ? extends ValueEquality<?>> valueEqualitiesMap, List<FieldDescription> tupleFields) {
        super(null);
    }

    @Override
    public CompositeEqualityWrapper<Tuple> wrap(Tuple obj) {
        return new CompositeEqualityWrapper<>(obj, this);
    }

}
