package org.someth2say.taijitu.tuple;

import org.someth2say.taijitu.ComparisonRuntime;
import org.someth2say.taijitu.compare.EqualityStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jordi Sola
 */
public class ComparableTuple extends Tuple implements Comparable<ComparableTuple> {

    private final ComparisonRuntime runtime;

    public ComparableTuple(Object[] values, final ComparisonRuntime runtime) {
        super(values);
        this.runtime = runtime;
    }

    public List<String> toStringList() {
        List<String> result = new ArrayList<>(size());
        int pos = 0;
        while (pos < this.size()) {
            result.add(this.getValue(pos).toString());
        }
        return result;
    }

    //Hashcode and equals should both be base on comparators
    // As for investigation, "comparator" is not the right name.
    // Eclipse Collections use the so called "HashingStrategy", that covers equals/hashcode contract
    // But I do not like this name, as implies "hashing".
    // I do prefer the term "EqualityStrategy"
    @Override
    public int hashCode() {
        int result = 1;
        for (int keyColumnIdx : runtime.getKeyColumnsIdxs()) {
            Object keyValue = getValue(keyColumnIdx);
            final EqualityStrategy<Object> comparator = runtime.getEqualityStrategy(keyValue.getClass());
            int keyHashCode = comparator.computeHashCode(keyValue);
            result = 31 * result + keyHashCode;
        }
        return result;
    }

    @Override
    public boolean equals(Object other) {
        return other != null && other instanceof ComparableTuple && equalKeys((ComparableTuple) other);
    }

    private boolean equalKeys(ComparableTuple other) {
        return equalColumns(other, runtime.getKeyColumnsIdxs());
    }

    public boolean equalsNonKeys(ComparableTuple other) {
        return equalColumns(other, runtime.getNonKeyColumnsIdxs());
    }

    private boolean equalColumns(ComparableTuple other, int[] columnsIdxs) {
        for (int columnIdx : columnsIdxs) {
            Object keyValue = getValue(columnIdx);
            Object otherKeyValue = other.getValue(columnIdx);
            final EqualityStrategy<Object> comparator = runtime.getEqualityStrategy(keyValue.getClass());
            if (!comparator.equals(keyValue, otherKeyValue)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int compareTo(ComparableTuple other) {
        int[] columnsIdxs = runtime.getKeyColumnsIdxs();
        for (int columnIdx : columnsIdxs) {
            Object keyValue = getValue(columnIdx);
            Object otherKeyValue = other.getValue(columnIdx);
            final EqualityStrategy<Object> comparator = runtime.getEqualityStrategy(keyValue.getClass());
            final int keyComparison = comparator.compare(keyValue, otherKeyValue);
            if (keyComparison !=0) {
                return keyComparison;
            }
        }
        return 0;
    }

    public int compareKeysTo(ComparableTuple other){
        return compareTo(other);
    }

}
