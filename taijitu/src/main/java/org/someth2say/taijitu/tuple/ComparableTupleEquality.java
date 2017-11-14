package org.someth2say.taijitu.tuple;

import org.someth2say.taijitu.compare.equality.composite.IComparableCompositeEquality;
import org.someth2say.taijitu.compare.equality.composite.ComparableCompositeEqualityWrapper;
import org.someth2say.taijitu.compare.equality.value.ComparableValueEquality;
import org.someth2say.taijitu.compare.equality.value.ValueEquality;
import org.someth2say.taijitu.source.FieldDescription;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Deprecated
public class ComparableTupleEquality extends AbstractTupleEquality implements IComparableCompositeEquality<Tuple> {

    public ComparableTupleEquality(Map<FieldDescription, ? extends ComparableValueEquality<?>> valueEqualitiesMap, List<FieldDescription> tupleFields) {
        super(null);
        // super(valueEqualitiesMap, tupleFields);
    }

    @Override
    public int compareTo(Tuple first, Tuple second) {
        LinkedHashMap<Integer, ValueEquality<?>> positionalEqualities = getPositionalEqualities();
        for (Map.Entry<Integer, ValueEquality<?>> entry : positionalEqualities.entrySet()) {
            Integer fieldPosition = entry.getKey();
            ComparableValueEquality equality = (ComparableValueEquality) entry.getValue();

            Object firstValue = first.getValue(fieldPosition);
            Object secondValue = second.getValue(fieldPosition);

            int compareResult = equality.compare(firstValue, secondValue);
            if (compareResult != 0) {
                return compareResult;
            }
        }
        return 0;
    }

    @Override
    public ComparableCompositeEqualityWrapper<Tuple> wrap(Tuple obj) {
        return new ComparableCompositeEqualityWrapper<>(obj, this);
    }
}

