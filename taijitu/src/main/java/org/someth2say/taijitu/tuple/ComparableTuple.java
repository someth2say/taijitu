package org.someth2say.taijitu.tuple;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.ComparisonRuntime;
import org.someth2say.taijitu.compare.equality.EqualityStrategy;
import org.someth2say.taijitu.config.EqualityConfig;
import org.someth2say.taijitu.registry.EqualityStrategyRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a {@link Tuple}, but having the capabilities to compare itself with other tuples, but using {@link EqualityStrategy} objects.
 *
 * @author Jordi Sola
 */
public class ComparableTuple extends Tuple implements Comparable<ComparableTuple> {
    private static final Logger logger = Logger.getLogger(ComparableTuple.class);

    private final ComparisonRuntime runtime;
    private final EqualityConfig[] equalityConfigs;

    public ComparableTuple(Object[] values, final ComparisonRuntime runtime, final EqualityConfig[] equalityConfigs) {
        super(values);
        this.runtime = runtime;
        this.equalityConfigs = equalityConfigs;
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
        int result = 1;
        for (int keyFieldIdx : runtime.getKeyFieldIdxs()) {
            Object keyValue = getValue(keyFieldIdx);
            final EqualityConfig equalityConfig = equalityConfigs[keyFieldIdx];
            final EqualityStrategy equalityStrategy = getEqualityStrategy(equalityConfig);
            int keyHashCode = equalityStrategy.computeHashCode(keyValue, equalityConfig.getEqualityParameters());
            result = 31 * result + keyHashCode;
        }
        return result;
    }

    @Override
    public boolean equals(Object other) {
        return other != null && other instanceof ComparableTuple && equalKeys((ComparableTuple) other);
    }

    private boolean equalKeys(ComparableTuple other) {
        return equalFields(other, runtime.getKeyFieldIdxs());
    }

    public boolean equalsNonKeys(ComparableTuple other) {
        return equalFields(other, runtime.getNonKeyFieldsIdxs());
    }

    private boolean equalFields(ComparableTuple other, int[] fieldIdxs) {
        for (int fieldIdx : fieldIdxs) {
            Object keyValue = getValue(fieldIdx);
            Object otherKeyValue = other.getValue(fieldIdx);
            FieldDescription fieldDescription = runtime.getCanonicalFields().get(fieldIdx);
            final EqualityConfig equalityConfig = equalityConfigs[fieldIdx];
            final EqualityStrategy equalityStrategy = getEqualityStrategy(equalityConfig);
            //TODO: Lazy logging
            logger.info("Comparing field: " + fieldDescription + " this: " + keyValue + "(" + keyValue.getClass().getName() + ") other: " + otherKeyValue + "(" + otherKeyValue.getClass().getName() + ") equalityStrategy: " + equalityStrategy.getName() + " config: " + equalityConfig.getEqualityParameters());
            if (!equalityStrategy.equals(keyValue, otherKeyValue, equalityConfig.getEqualityParameters())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int compareTo(ComparableTuple other) {
        int[] fieldIdxs = runtime.getKeyFieldIdxs();
        for (int fieldIdx : fieldIdxs) {
            Object keyValue = getValue(fieldIdx);
            Object otherKeyValue = other.getValue(fieldIdx);
            final EqualityConfig equalityConfig = equalityConfigs[fieldIdx];
            final EqualityStrategy equalityStrategy = getEqualityStrategy(equalityConfig);
            final int keyComparison = equalityStrategy.compare(keyValue, otherKeyValue, equalityConfig.getEqualityParameters());
            if (keyComparison != 0) {
                return keyComparison;
            }
        }
        return 0;
    }



    private EqualityStrategy getEqualityStrategy(final EqualityConfig equalityConfig) {
        final String equalityName = equalityConfig.getName();
        return EqualityStrategyRegistry.getEqualityStrategy(equalityName);
    }

    public int compareKeysTo(ComparableTuple other) {
        return compareTo(other);
    }

}
