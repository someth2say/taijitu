package org.someth2say.taijitu.tuple;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.compare.equality.tuple.AbstractStructureEquality;
import org.someth2say.taijitu.compare.equality.tuple.StructureEquality;
import org.someth2say.taijitu.compare.equality.tuple.StructureEqualityWrapper;
import org.someth2say.taijitu.compare.equality.value.ValueEquality;

import java.util.Map;

public class TupleExternalEquality extends AbstractStructureEquality<Tuple> implements StructureEquality<Tuple> {
    private static final Logger logger = Logger.getLogger(TupleExternalEquality.class);


    public TupleExternalEquality(Map<FieldDescription, ValueEquality<?>> valueEqualitiesMap) {
        super(valueEqualitiesMap);
    }

    @Override
    public boolean equals(Tuple obj, Tuple other) {
        return equalFields(obj, other);
    }

    private boolean equalFields(Tuple canonical, Tuple provided) {

        for (Map.Entry<FieldDescription, ValueEquality<?>> mapEntry : getValueEqualitiesMap().entrySet()) {
            FieldDescription fieldDescription = mapEntry.getKey();
            Object keyValue = extractFieldValue(canonical, fieldDescription);
            Object otherKeyValue = extractFieldValue(provided, fieldDescription);

            ValueEquality equality = mapEntry.getValue();
            @SuppressWarnings("unchecked")
            boolean equals = equality.equals(keyValue, otherKeyValue);
            if (logger.isDebugEnabled()) {
                logger.debug(keyValue + "<=>" + otherKeyValue + "(" + otherKeyValue.getClass().getName() + ") stream: " + equality.getName() + " result: " + equals);
            }
            if (!equals) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Difference found: Field:" + fieldDescription + " Values: " + keyValue + "<=>" + otherKeyValue);
                }
                return false;
            }
        }
        return true;
    }

    @Override
    public Object extractFieldValue(Tuple tuple, FieldDescription fieldDescription) {
        return tuple.getValue(fieldDescription.getPosition());
    }


    @Override
    public int hashCode(Tuple obj) {
        //TODO:
    }

    @Override
    public StructureEqualityWrapper<Tuple> wrap(Tuple obj) {
        //TODO;
    }

}
