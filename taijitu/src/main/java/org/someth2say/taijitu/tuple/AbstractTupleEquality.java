package org.someth2say.taijitu.tuple;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.compare.equality.composite.CompositeEquality;
import org.someth2say.taijitu.compare.equality.composite.ExtractorAndEquality;
import org.someth2say.taijitu.compare.equality.value.ValueEquality;
import org.someth2say.taijitu.source.FieldDescription;

import java.util.*;

@Deprecated
public abstract class AbstractTupleEquality extends CompositeEquality<Tuple> {
    //This is the list of ALL fields that will come in the structure.
    private final LinkedHashMap<Integer, ValueEquality<?>> positionalEqualities;
    private static final Logger logger = Logger.getLogger(AbstractTupleEquality.class);

    public AbstractTupleEquality(List<ExtractorAndEquality<Tuple, ?>> extractorsAndEqualities) {
        super(extractorsAndEqualities);
        //this.positionalEqualities = computeEqualityPositions(tupleFields);
        this.positionalEqualities = null;
    }

//    private LinkedHashMap<Integer, ValueEquality<?>> computeEqualityPositions(List<FieldDescription> tupleFields) {
//        Map<FieldDescription, ? extends ValueEquality<?>> valueEqualitiesMap = getValueEqualitiesMap();
//        LinkedHashMap<Integer, ValueEquality<?>> result = new LinkedHashMap<>(valueEqualitiesMap.size());
//        for (Map.Entry<FieldDescription, ? extends ValueEquality<?>> entry : valueEqualitiesMap.entrySet()) {
//            int fieldIdx = tupleFields.indexOf(entry.getKey());
//            ValueEquality<?> fieldEquality = entry.getValue();
//            result.put(fieldIdx, fieldEquality);
//        }
//        return result;
//    }


    protected LinkedHashMap<Integer, ValueEquality<?>> getPositionalEqualities() {
        return positionalEqualities;
    }

    @Override
    public boolean equals(Tuple first, Tuple second) {
        for (Map.Entry<Integer, ? extends ValueEquality<?>> entry : getPositionalEqualities().entrySet()) {
            Integer fieldPosition = entry.getKey();
            ValueEquality equality = entry.getValue();

            Object firstValue = first.getValue(fieldPosition);
            Object secondValue = second.getValue(fieldPosition);

            @SuppressWarnings("unchecked")
            boolean equals = equality.equals(firstValue, secondValue);
            if (logger.isDebugEnabled()) {
                logger.debug(firstValue + "<=>" + secondValue + "(" + firstValue.getClass().getName() + ") equality: " + equality + " result: " + equals);
            }
            if (!equals) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Difference found: Field#:" + fieldPosition + " Values: " + firstValue + "<=>" + secondValue);
                }
                return false;
            }
        }
        return true;
    }


    @Override
    public int hashCode(Tuple obj) {
        int result = 1;
        for (Map.Entry<Integer, ? extends ValueEquality<?>> entry : getPositionalEqualities().entrySet()) {
            Integer fieldPosition = entry.getKey();
            ValueEquality equality = entry.getValue();
            Object value = obj.getValue(fieldPosition);
            @SuppressWarnings("unchecked")
            int hashCode = value != null ? equality.computeHashCode(value) : 0;
            result = 31 * result + hashCode;
        }
        return result;
    }


}
