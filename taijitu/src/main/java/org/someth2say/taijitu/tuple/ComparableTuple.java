package org.someth2say.taijitu.tuple;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.ComparisonContext;
import org.someth2say.taijitu.compare.equality.value.SortedValueEquality;
import org.someth2say.taijitu.config.interfaces.IEqualityCfg;
import org.someth2say.taijitu.registry.ValueEqualityRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a {@link Tuple}, but delegating capabilities to compare itself with other tuples to  {@link SortedValueEquality} objects.
 *
 * @author Jordi Sola
 */
public class ComparableTuple extends Tuple implements Comparable<ComparableTuple> {
    private static final Logger logger = Logger.getLogger(ComparableTuple.class);

    private final ComparisonContext context;

    public ComparableTuple(Object[] values, final ComparisonContext context) {
        super(values);
        this.context = context;
    }

    //TODO: Consider passing those three fields (or a data class containing them) instead of the ComparisonContext. In other words, create an tuple stateful Comparator object.
    private List<IEqualityCfg> getEqualityConfigs() {
        return context.getIEqualitys();
    }

    private List<FieldDescription> getCanonicalFields() {
        return context.getCanonicalFields();
    }

    private int[] getKeyFieldIdxs() {
        return context.getKeyFieldIdxs();
    }


    public List<String> toStringList() {
        List<String> result = new ArrayList<>(size());
        int pos = 0;
        while (pos < this.size()) {
            result.add(this.getValue(pos).toString());
        }
        return result;
    }

    @Override
    public int hashCode() {
        List<IEqualityCfg> equalityConfigIfaces = getEqualityConfigs();
        int result = 1;
        for (int keyFieldIdx : getKeyFieldIdxs()) {
            Object keyValue = getValue(keyFieldIdx);
            final IEqualityCfg equalityConfigIface = equalityConfigIfaces.get(keyFieldIdx);
            @SuppressWarnings("rawtypes") final SortedValueEquality sortedValueEquality = getEqualityStrategy(equalityConfigIface);
            @SuppressWarnings("unchecked")
            int keyHashCode = sortedValueEquality.computeHashCode(keyValue, equalityConfigIface.getEqualityParameters());
            result = 31 * result + keyHashCode;
        }
        return result;
    }

    @Override
    public boolean equals(Object other) {
        return other != null && other instanceof ComparableTuple && equalKeys((ComparableTuple) other);
    }

    private boolean equalKeys(ComparableTuple other) {
        return equalFields(other, getKeyFieldIdxs());
    }

    public boolean equalsNonKeys(ComparableTuple other) {
        return equalFields(other, context.getNonKeyFieldsIdxs());
    }

    private boolean equalFields(ComparableTuple other, int[] fieldIdxs) {
        List<IEqualityCfg> equalityConfigIfaces = getEqualityConfigs();
        for (int fieldIdx : fieldIdxs) {
            Object keyValue = getValue(fieldIdx);
            Object otherKeyValue = other.getValue(fieldIdx);
            FieldDescription fieldDescription = getCanonicalFields().get(fieldIdx);
            final IEqualityCfg equalityConfigIface = equalityConfigIfaces.get(fieldIdx);
            @SuppressWarnings("rawtypes") final SortedValueEquality sortedValueEquality = getEqualityStrategy(equalityConfigIface);
            if (logger.isDebugEnabled()) {
                logger.debug(fieldDescription + ":" + keyValue + "<=>" + otherKeyValue + "(" + otherKeyValue.getClass().getName() + ") stream: " + sortedValueEquality.getName() + " config: " + equalityConfigIface.getEqualityParameters());
            }
            @SuppressWarnings("unchecked")
            boolean equals = sortedValueEquality.equals(keyValue, otherKeyValue, equalityConfigIface.getEqualityParameters());
            if (!equals) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Difference found: Field " + fieldDescription.getName() + " Values: " + keyValue + "<=>" + otherKeyValue);
                }
                return false;
            }
        }
        return true;
    }

    @Override
    public int compareTo(ComparableTuple other) {
        List<IEqualityCfg> equalityConfigIfaces = getEqualityConfigs();
        int[] fieldIdxs = getKeyFieldIdxs();
        for (int fieldIdx : fieldIdxs) {
            Object keyValue = getValue(fieldIdx);
            Object otherKeyValue = other.getValue(fieldIdx);
            final IEqualityCfg equalityConfigIface = equalityConfigIfaces.get(fieldIdx);
            @SuppressWarnings("rawtypes") final SortedValueEquality sortedValueEquality = getEqualityStrategy(equalityConfigIface);
            @SuppressWarnings("unchecked") final int keyComparison = sortedValueEquality.compare(keyValue, otherKeyValue, equalityConfigIface.getEqualityParameters());
            if (keyComparison != 0) {
                return keyComparison;
            }
        }
        return 0;
    }


    private SortedValueEquality<?> getEqualityStrategy(final IEqualityCfg equalityConfigIface) {
        final String equalityName = equalityConfigIface.getName();
        return ValueEqualityRegistry.getValueEqualityType(equalityName);
    }

    public int compareKeysTo(ComparableTuple other) {
        return compareTo(other);
    }

}
