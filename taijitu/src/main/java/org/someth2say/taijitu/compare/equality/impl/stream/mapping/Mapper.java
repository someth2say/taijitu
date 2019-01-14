package org.someth2say.taijitu.compare.equality.impl.stream.mapping;

import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.compare.equality.aspects.external.Hasher;
import org.someth2say.taijitu.compare.result.Difference;
import org.someth2say.taijitu.compare.result.Unequal;
import org.someth2say.taijitu.compare.equality.wrapper.HashableWrapper;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
/**
 * Runnable class that is responsible for too many things:
 * - Iterate a source of elements
 * - Keep a hash table for those elements (using a given Hasher)
 * - If two elements have same hash, use the given Equalizer to determine equality
 * - If elements are equals, both are consumed.
 * - Else, an 'Unequals' object is built
 * 
 * This class should be deprecated in favor of stream-based implementation.
 */
class Mapper<T> implements Runnable {

    private final Iterator<T> source;
    private final Map<HashableWrapper<T>, OrdinalAndComposite<T>> sharedMap;
    private final List<Difference<?>> result;
    private final int ordinal;
    private final Hasher<T> categorizer;
    private final Equalizer<T> equalizer;

    public Mapper(final Iterator<T> source,
                  final Map<HashableWrapper<T>, OrdinalAndComposite<T>> sharedMap,
                  final List<Difference<?>> result, int ordinal, Hasher<T> categorizer,
                  Equalizer<T> equalizer) {
        this.source = source;
        this.sharedMap = sharedMap;
        this.result = result;
        this.ordinal = ordinal;
        this.categorizer = categorizer;
        this.equalizer = equalizer;
    }

    @Override
    public void run() {
        T thisRecord = getNextRecordOrNull(source);
        while (thisRecord != null) {
            Unequal<T> unequal = map(thisRecord, ordinal, categorizer, sharedMap, equalizer);
            if (unequal != null) {
                result.add(unequal);
            }
            thisRecord = getNextRecordOrNull(source);
        }
    }

    public static <T> Unequal<T> map(T composite, int ordinal, Hasher<T> hasher, Map<HashableWrapper<T>, OrdinalAndComposite<T>> sharedMap, Equalizer<T> equalizer) {
        OrdinalAndComposite<T> oac = new OrdinalAndComposite<>(ordinal, composite);
        return map(oac, hasher, sharedMap, equalizer);
    }

    public static <T> Unequal<T> map(OrdinalAndComposite<T> thisOaC, Hasher<T> hasher,
                                     Map<HashableWrapper<T>, OrdinalAndComposite<T>> sharedMap, 
                                     Equalizer<T> equalizer) {
        HashableWrapper<T> wrapper = new HashableWrapper<>(thisOaC.getComposite(), hasher);
        OrdinalAndComposite<T> otherOaC = sharedMap.putIfAbsent(wrapper, new OrdinalAndComposite<>(thisOaC.getOrdinal(), thisOaC.getComposite()));
        if (otherOaC != null) {
            // we have a key match ...
            sharedMap.remove(wrapper);
            Unequal<T> unequal = getUnequal(equalizer, thisOaC, otherOaC);
			return unequal;
        }
        return null;
    }

    private static <T> Unequal<T> getUnequal(Equalizer<T> equalizer, OrdinalAndComposite<T> first, OrdinalAndComposite<T> second) {
        Unequal<T> unequal;
        if (first.getOrdinal() < second.getOrdinal()) {
            unequal = equalizer.asUnequal(first.getComposite(), second.getComposite());
        } else {
            unequal = equalizer.asUnequal(second.getComposite(), first.getComposite());
        }
        return unequal;
    }

    private static <T> T getNextRecordOrNull(Iterator<T> resultSetSource) {
        return resultSetSource.hasNext() ? resultSetSource.next() : null;
    }
}
