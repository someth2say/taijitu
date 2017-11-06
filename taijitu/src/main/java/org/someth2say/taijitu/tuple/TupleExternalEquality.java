package org.someth2say.taijitu.tuple;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.compare.equality.value.ValueEquality;
import org.someth2say.taijitu.compare.equality.external.EqualityWrapper;
import org.someth2say.taijitu.compare.equality.external.ExternalEquality;

import java.util.List;

public class TupleExternalEquality implements ExternalEquality<Tuple> {
    private static final Logger logger = Logger.getLogger(TupleExternalEquality.class);

    private final List<ValueEquality<?>> fieldEqualities;
    private final List<Object> equalityParams;
    private final int[] compareIdx;

    //TODO All three collections should have matching elements, so maybe we can wrapp all into a single object...
    public TupleExternalEquality(List<ValueEquality<?>> fieldEqualities, List<Object> equalityParams, int[] compareIdx) {
        this.fieldEqualities = fieldEqualities;
        this.equalityParams = equalityParams;
        this.compareIdx = compareIdx;
    }

    @Override
    public boolean equals(Tuple obj, Tuple other) {
        return equalFields(obj, other);
    }

    private boolean equalFields(Tuple canonical, Tuple provided) {

        for (int fieldIdx = 0; fieldIdx < compareIdx.length; fieldIdx++) {

            Object keyValue = canonical.getValue(compareIdx[fieldIdx]);
            Object otherKeyValue = provided.getValue(compareIdx[fieldIdx]);

            ValueEquality valueEquality = fieldEqualities.get(fieldIdx);
            Object equalityParameters = equalityParams.get(fieldIdx);
            @SuppressWarnings("unchecked")
            boolean equals = valueEquality.equals(keyValue, otherKeyValue, equalityParameters);
            if (logger.isDebugEnabled()) {
                logger.debug(keyValue + "<=>" + otherKeyValue + "(" + otherKeyValue.getClass().getName() + ") strategy: " + valueEquality.getName() + " config: " + equalityParameters + " result: " + equals);
            }
            if (!equals) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Difference found: Field Idx" + compareIdx[fieldIdx] + " Values: " + keyValue + "<=>" + otherKeyValue);
                }
                return false;
            }
        }
        return true;
    }


    @Override
    public int hashCode(Tuple obj) {
        return 0;
    }

    @Override
    public EqualityWrapper<Tuple> wrap(Tuple obj) {
        return null;
    }

}
}
