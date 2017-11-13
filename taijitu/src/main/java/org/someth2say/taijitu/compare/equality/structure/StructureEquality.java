package org.someth2say.taijitu.compare.equality.structure;

import org.someth2say.taijitu.compare.equality.value.ValueEquality;
import org.someth2say.taijitu.util.Pair;

import java.util.List;
import java.util.function.Function;

/**
 * This is the simplest class of CompositeEquality. It assumes that:
 * <OL><LI>Both sides of equality are the same type and</LI>
 * <li>Both sides of equality are using the same extractors</li></OL>
 *
 * @param <T>
 */
public class StructureEquality<T, Q> implements IStructureEquality<T, Q> {

    final List<ExtractorsAndEquality<T, Q, ?>> extractorsAndEqualities;

    public StructureEquality(List<ExtractorsAndEquality<T, Q, ?>> extractorsAndEqualities) {
        this.extractorsAndEqualities = extractorsAndEqualities;
    }


    /**
     * Default equality is ordered given the list of extractors.
     */
    @Override
    public boolean equals(T first, Q second) {
        return extractorsAndEqualities.stream().allMatch(eae -> valueEquals(first, second, eae));
    }

    private <V> boolean valueEquals(T first, Q second, ExtractorsAndEquality<T, Q, V> eae) {
        Pair<Function<T, V>, Function<Q, V>> extractors = eae.getExtractors();
        ValueEquality<V> equality = eae.getEquality();
        V firstValue = extractors.getLeft().apply(first);
        V secondValue = extractors.getRight().apply(second);
        return equality.equals(firstValue, secondValue);
    }

    @Override
    public int hashCode(T obj) {
        int result = 1;
        for (ExtractorsAndEquality<T, Q, ?> eae : extractorsAndEqualities) {
            result = 31 * result + valueHashCode(obj, eae);
        }
        return result;

    }

    private <T, V> int valueHashCode(T obj, ExtractorsAndEquality<T, ?, V> eae) {
        Function<T, V> key = eae.getExtractors().getKey();
        V value = key.apply(obj);
        return eae.getEquality().computeHashCode(value);
    }

    @Override
    public StructureEqualityWrapper<T> wrap(T obj) {
        return new StructureEqualityWrapper<>(obj, this);
    }
}
