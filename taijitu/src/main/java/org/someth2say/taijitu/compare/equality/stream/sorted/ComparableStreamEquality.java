package org.someth2say.taijitu.compare.equality.stream.sorted;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.compare.equality.ComparableCategorizerEquality;
import org.someth2say.taijitu.compare.equality.Equality;
import org.someth2say.taijitu.compare.equality.stream.AbstractStreamEquality;
import org.someth2say.taijitu.compare.result.Mismatch;
import org.someth2say.taijitu.discarter.TimeBiDiscarter;
import org.someth2say.taijitu.ui.config.interfaces.IStrategyCfg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import static org.someth2say.taijitu.compare.equality.stream.ComparisonResult.*;

/**
 * Created by Jordi Sola on 02/03/2017.
 */
public class ComparableStreamEquality<T> extends AbstractStreamEquality<T> {

    private static final Logger logger = LoggerFactory.getLogger(ComparableStreamEquality.class);

    public ComparableStreamEquality(Equality<T> equality, ComparableCategorizerEquality<T> categorizer) {
        super(equality, categorizer);
    }

    @Override
    public List<Mismatch> match(Stream<T> source, Object sourceId, Stream<T> target, Object targetId) {
        return compare(source, sourceId, target, targetId, getCategorizer(), getEquality());
    }

    public static <T> List<Mismatch> compare(Map<Object, Stream<T>> streams, ComparableCategorizerEquality<T> categorizer, Equality<T> equality) {
        if (streams.size() < 2)
            throw new RuntimeException("Need at least two streams to compare");

        if (streams.size() > 2)
            logger.info("Provided {} streams, but only 2 first will be compared.", streams.size());


        Iterator<Entry<Object, Stream<T>>> iterator = streams.entrySet().iterator();
        Entry<Object, Stream<T>> source = iterator.next();
        Entry<Object, Stream<T>> target = iterator.next();

        return compare(source.getValue(), source.getKey(), target.getValue(), target.getKey(), categorizer, equality);
    }

    public static <T> List<Mismatch> compare(Stream<T> source, Object sourceId, Stream<T> target, Object targetId, ComparableCategorizerEquality<T> categorizer, Equality<T> equality) {
//        SimpleComparisonResult result = new SimpleComparisonResult();

        List<Mismatch> newresult = new ArrayList<>();
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
                addMissing(newresult, categorizer, targetRecord);
                targetRecord = getNextRecordOrNull(targetIt);
                recordCount++;
            } else if (keyComparison < 0) {
                // SourceCfg is before target -> source record is not in target stream
                addMissing(newresult, categorizer, sourceRecord);
                sourceRecord = getNextRecordOrNull(sourceIt);
                recordCount++;
            } else {
                // same Keys
                // TODO: Use equality.difference
                List<Mismatch> differences = equality.differences(sourceRecord, targetRecord);
                // Records are different
                if (differences != null && !differences.isEmpty()) {
                    addDifference(newresult, equality, sourceRecord, targetRecord, differences);
                }
                sourceRecord = getNextRecordOrNull(sourceIt);
                recordCount++;
                targetRecord = getNextRecordOrNull(targetIt);
                recordCount++;
            }
        }

        // At least, one stream is fully consumed, so add every other stream's element
        // to "missing"
        while (sourceIt.hasNext()) {
            addMissing(newresult, categorizer, sourceIt.next());
            timedLogger.accept("Finalizing source {}, {} records so far...", new Object[]{sourceId, ++recordCount});
        }
        while (targetIt.hasNext()) {
            addMissing(newresult, categorizer, targetIt.next());
            timedLogger.accept("Finalizing source {}, {} records so far...", new Object[]{targetId, ++recordCount});
        }

        return newresult;
    }

}
