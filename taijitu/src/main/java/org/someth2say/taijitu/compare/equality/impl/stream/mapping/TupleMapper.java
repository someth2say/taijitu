package org.someth2say.taijitu.compare.equality.impl.stream.mapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.compare.equality.aspects.external.Hasher;
import org.someth2say.taijitu.compare.equality.wrapper.IHashableWraper;
import org.someth2say.taijitu.compare.result.Difference;
import org.someth2say.taijitu.compare.result.Unequal;
import org.someth2say.taijitu.discarter.TimeBiDiscarter;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

class TupleMapper<T> implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(TupleMapper.class);

    private final Iterator<T> source;
    private final Map<IHashableWraper<T, ?>, OrdinalAndComposite<T>> sharedMap;
    private final List<Difference<?>> result;
    private final int ordinal;
    private final Hasher<T> categorizer;
    private final Equalizer<T> equalizer;

    public TupleMapper(final Iterator<T> source,
                final Map<IHashableWraper<T, ?>, OrdinalAndComposite<T>> sharedMap,
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
        TimeBiDiscarter<String, Object[]> timedLogger = new TimeBiDiscarter<>(1000, logger::debug);
        int recordCount = 0;
        T thisRecord = getNextRecordOrNull(source);
        while (thisRecord != null) {
            recordCount++;
            Unequal<T> unequal = map(thisRecord, ordinal, timedLogger, recordCount, categorizer, sharedMap, equalizer);
            if (unequal != null) {
                result.add(unequal);
            }
            thisRecord = getNextRecordOrNull(source);
        }
    }

    public static <T> Unequal<T> map(T composite, int ordinal, TimeBiDiscarter<String, Object[]> timedLogger, int recordCount, Hasher<T> mapper, Map<IHashableWraper<T, ?>, OrdinalAndComposite<T>> sharedMap, Equalizer<T> equalizer) {
        OrdinalAndComposite<T> oac = new OrdinalAndComposite<>(ordinal, composite);
        return map(oac, timedLogger, recordCount, mapper, sharedMap, equalizer);
    }

    public static <T> Unequal<T> map(OrdinalAndComposite<T> thisOaC,
                                     TimeBiDiscarter<String, Object[]> timedLogger, int recordCount, Hasher<T> categorizer,
                                     Map<IHashableWraper<T, ?>, OrdinalAndComposite<T>> sharedMap, Equalizer<T> equalizer) {
        timedLogger.accept("Processed {} records so far.", new Object[]{recordCount});
        IHashableWraper<T, ?> wraped = categorizer.wrap(thisOaC.getComposite());
        OrdinalAndComposite<T> otherOaC = sharedMap.putIfAbsent(wraped, new OrdinalAndComposite<>(thisOaC.getOrdinal(), thisOaC.getComposite()));
        if (otherOaC != null) {
            // we have a key match ...
            sharedMap.remove(wraped);
            return getUnequal(equalizer, thisOaC, otherOaC);
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
