package org.someth2say.taijitu.tuple;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.ComparisonContext;
import org.someth2say.taijitu.compare.equality.EqualityStrategy;
import org.someth2say.taijitu.config.interfaces.IEqualityCfg;
import org.someth2say.taijitu.registry.EqualityStrategyRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a {@link Tuple}, but delegating capabilities to compare itself with other tuples to  {@link EqualityStrategy} objects.
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

    //TODO: Consider passing those three fields (or a data class containing them) instead of the ComparisonContext
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
            @SuppressWarnings("rawtypes")
			final EqualityStrategy equalityStrategy = getEqualityStrategy(equalityConfigIface);
            @SuppressWarnings("unchecked")
			int keyHashCode = equalityStrategy.computeHashCode(keyValue, equalityConfigIface.getEqualityParameters());
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

    @SuppressWarnings("unchecked")
	private boolean equalFields(ComparableTuple other, int[] fieldIdxs) {
        List<IEqualityCfg> equalityConfigIfaces = getEqualityConfigs();
        for (int fieldIdx : fieldIdxs) {
            Object keyValue = getValue(fieldIdx);
            Object otherKeyValue = other.getValue(fieldIdx);
            FieldDescription fieldDescription = getCanonicalFields().get(fieldIdx);
            final IEqualityCfg equalityConfigIface = equalityConfigIfaces.get(fieldIdx);
            @SuppressWarnings("rawtypes")
			final EqualityStrategy equalityStrategy = getEqualityStrategy(equalityConfigIface);
            //TODO: Lazy logging
            logger.debug(fieldDescription + ":" + keyValue + "<=>" + otherKeyValue + "(" + otherKeyValue.getClass().getName() + ") strategy: " + equalityStrategy.getName() + " config: " + equalityConfigIface.getEqualityParameters());
            if (!equalityStrategy.equals(keyValue, otherKeyValue, equalityConfigIface.getEqualityParameters())) {
                logger.debug("Difference found: Field " + fieldDescription.getName() + " Values: " + keyValue + "<=>" + otherKeyValue);
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
            @SuppressWarnings("rawtypes")
			final EqualityStrategy equalityStrategy = getEqualityStrategy(equalityConfigIface);
            @SuppressWarnings("unchecked")
            final int keyComparison = equalityStrategy.compare(keyValue, otherKeyValue, equalityConfigIface.getEqualityParameters());
            if (keyComparison != 0) {
                return keyComparison;
            }
        }
        return 0;
    }


    private EqualityStrategy<?> getEqualityStrategy(final IEqualityCfg equalityConfigIface) {
        final String equalityName = equalityConfigIface.getName();
        return EqualityStrategyRegistry.getEqualityStrategy(equalityName);
    }

    public int compareKeysTo(ComparableTuple other) {
        return compareTo(other);
    }

}
