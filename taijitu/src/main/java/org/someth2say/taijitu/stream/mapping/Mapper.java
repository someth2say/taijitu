package org.someth2say.taijitu.stream.mapping;

import org.someth2say.taijitu.equality.aspects.external.Hasher;
import org.someth2say.taijitu.equality.wrapper.HashableWrapper;
import org.someth2say.taijitu.equality.explain.Difference;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Runnable class that is responsible for too many things:
 * - Iterate a source of elements
 * - Keep a hash table for those elements (using a given Hasher)
 * - If two elements have same hash, use the given Equalizer to determine equality
 * - If elements are equals, both are consumed.
 * - Else, an 'Unequals' object is built
 * <p>
 * This class should be deprecated in favor of stream-based implementation.
 */
@Deprecated
class Mapper<T> implements Runnable {

    private final Iterator<T> source;
    private final Map<HashableWrapper<T>, OrdinalAndComposite<T>> sharedMap;
    private final List<Difference<?>> result;
    private final int ordinal;
    private final Hasher<T> categorizer;

    public Mapper(final Iterator<T> source,
                  final Map<HashableWrapper<T>, OrdinalAndComposite<T>> sharedMap,
                  final List<Difference<?>> result, int ordinal, Hasher<T> categorizer) {
        this.source = source;
        this.sharedMap = sharedMap;
        this.result = result;
        this.ordinal = ordinal;
        this.categorizer = categorizer;
    }

    @Override
    public void run() {
        T thisRecord = getNextRecordOrNull(source);
        while (thisRecord != null) {
            map(thisRecord, ordinal, categorizer, sharedMap).map(result::add);
            thisRecord = getNextRecordOrNull(source);
        }
    }

    public static <T> Stream<Difference> map(T composite, int ordinal, Hasher<T> hasher, Map<HashableWrapper<T>, OrdinalAndComposite<T>> sharedMap) {
        OrdinalAndComposite<T> oac = new OrdinalAndComposite<>(ordinal, composite);
        return HashingStreamEqualizer.map(oac, hasher, sharedMap);
    }

    private static <T> T getNextRecordOrNull(Iterator<T> resultSetSource) {
        return resultSetSource.hasNext() ? resultSetSource.next() : null;
    }
}
