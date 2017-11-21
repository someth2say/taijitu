package org.someth2say.taijitu.compare.equality.stream.sorted;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.compare.equality.ComparableCategorizerEquality;
import org.someth2say.taijitu.compare.equality.ComparableEquality;
import org.someth2say.taijitu.compare.equality.Equality;
import org.someth2say.taijitu.compare.equality.stream.AbstractStreamEquality;
import org.someth2say.taijitu.compare.result.ComparisonResult;
import org.someth2say.taijitu.compare.result.Mismatch;
import org.someth2say.taijitu.compare.result.SimpleComparisonResult;
import org.someth2say.taijitu.discarter.TimeBiDiscarter;
import org.someth2say.taijitu.ui.config.interfaces.IStrategyCfg;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.stream.Stream;

/**
 * Created by Jordi Sola on 02/03/2017.
 */
public class ComparableStreamEquality<T> extends AbstractStreamEquality<T> {
    public static final String NAME = "sorted";
    private static final Logger logger = LoggerFactory.getLogger(ComparableStreamEquality.class);

    public ComparableStreamEquality(Equality<T> equality, ComparableCategorizerEquality<T> categorizer) {
        super(equality, categorizer);
    }

    @Override
    public String getName() {
        return NAME;
    }

    public ComparisonResult<T> match(Stream<T> source, Object sourceId, Stream<T> target, Object targetId) {
        return compare(source, sourceId, target, targetId, getCategorizer(), getEquality());
    }

    public static <T> ComparisonResult<T> compare(Map<Object, Stream<T>> streams,  ComparableCategorizerEquality<T> categorizer, Equality<T> equality) {
        if (streams.size() < 2)
            throw new RuntimeException("Need at least two streams to compare");

        if (streams.size() > 2)
            logger.info("Provided {} streams, but only 2 first will be compared.", streams.size());


        Iterator<Entry<Object, Stream<T>>> iterator = streams.entrySet().iterator();
        Entry<Object, Stream<T>> source = iterator.next();
        Entry<Object, Stream<T>> target = iterator.next();

        return compare(source.getValue(), source.getKey(), target.getValue(), target.getKey(), categorizer, equality);
    }

    public static <T> ComparisonResult<T> compare(Stream<T> source, Object sourceId, Stream<T> target, Object targetId, ComparableCategorizerEquality<T> categorizer, Equality<T> equality) {
        SimpleComparisonResult<T> result = new SimpleComparisonResult<>();

        Iterator<T> sourceIt = source.iterator();
        Iterator<T> targetIt = target.iterator();

        int recordCount = 0;
        T sourceRecord = getNextRecordOrNull(sourceIt);
        recordCount++;
        T targetRecord = getNextRecordOrNull(targetIt);
        recordCount++;

        TimeBiDiscarter<String, Object[]> timedLogger = new TimeBiDiscarter<>(1000, logger::debug);
        while (sourceRecord != null && targetRecord != null) {
            timedLogger.accept("Processing {} records so far...", new Object[]{recordCount});
            int keyComparison = categorizer.compare(sourceRecord, targetRecord);
            if (keyComparison > 0) {
                // SourceCfg is after target -> target record is not in source stream
                result.addDisjoint(categorizer, targetId, targetRecord);
                targetRecord = getNextRecordOrNull(targetIt);
                recordCount++;
            } else if (keyComparison < 0) {
                // SourceCfg is before target -> source record is not in target stream
                result.addDisjoint(categorizer, sourceId, sourceRecord);
                sourceRecord = getNextRecordOrNull(sourceIt);
                recordCount++;
            } else {
                // same Keys
                // TODO: Use equality.difference
                Collection<Mismatch> differences = equality.differences(sourceRecord, targetRecord);
                // Records are different
                if (differences != null && !differences.isEmpty()) {
                   differences.forEach(d->result.addMismatch(equality,d));
                }
//                if (!equality.equals(sourceRecord, targetRecord)) {
//                    result.addDifference(equality, sourceId, sourceRecord, targetId, targetRecord);
//                }
                sourceRecord = getNextRecordOrNull(sourceIt);
                recordCount++;
                targetRecord = getNextRecordOrNull(targetIt);
                recordCount++;
            }
        }

        // At least, one stream is fully consumed, so add every other stream's element
        // to "missing"
        while (sourceIt.hasNext()) {
            result.addDisjoint(categorizer, sourceId, sourceIt.next());
            timedLogger.accept("Finalizing source {}, {} records so far...", new Object[]{sourceId, ++recordCount});
        }
        while (targetIt.hasNext()) {
            result.addDisjoint(categorizer, targetId, targetIt.next());
            timedLogger.accept("Finalizing source {}, {} records so far...", new Object[]{targetId, ++recordCount});
        }

        return result;
    }

    public static IStrategyCfg defaultConfig() {
        return () -> ComparableStreamEquality.NAME;
    }

}
