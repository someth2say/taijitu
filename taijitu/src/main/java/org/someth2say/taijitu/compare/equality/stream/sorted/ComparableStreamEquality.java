package org.someth2say.taijitu.compare.equality.stream.sorted;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.compare.equality.external.ComparatorEquality;
import org.someth2say.taijitu.compare.equality.external.Equality;
import org.someth2say.taijitu.compare.equality.stream.AbstractStreamEquality;
import org.someth2say.taijitu.compare.result.Mismatch;
import org.someth2say.taijitu.discarter.TimeBiDiscarter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static org.someth2say.taijitu.compare.equality.stream.MismatchHelper.*;

/**
 * Created by Jordi Sola on 02/03/2017.
 */
public class ComparableStreamEquality<T> extends AbstractStreamEquality<T, ComparatorEquality<T>> implements org.someth2say.taijitu.compare.equality.stream.StreamEquality<T> {

    private static final Logger logger = LoggerFactory.getLogger(ComparableStreamEquality.class);

    public ComparableStreamEquality(Equality<T> equality, ComparatorEquality<T> categorizer) {
        super(equality, categorizer);
    }

    @Override
    public List<Mismatch<?>> underlyingDiffs(Stream<T> source, Stream<T> target) {
        return compare(source, target, getOther(), getEquality());
    }

    public static <T> List<Mismatch<?>> compare(Stream<T> source, Stream<T> target, ComparatorEquality<T> comparer, Equality<T> equality) {
        List<Mismatch<?>> newresult = new ArrayList<>();
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
            int keyComparison = comparer.compare(sourceRecord, targetRecord);
            if (keyComparison > 0) {
                // SourceCfg is after target -> target record is not in source stream
                addMissing(newresult, comparer, targetRecord);
                targetRecord = getNextRecordOrNull(targetIt);
                recordCount++;
            } else if (keyComparison < 0) {
                // SourceCfg is before target -> source record is not in target stream
                addMissing(newresult, comparer, sourceRecord);
                sourceRecord = getNextRecordOrNull(sourceIt);
                recordCount++;
            } else {
                // same Keys
                List<Mismatch<?>> differences = equality.underlyingDiffs(sourceRecord, targetRecord);
                if (differences != null && !differences.isEmpty()) {
                    // Records are different
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
        recordCount = flushMissings(comparer, newresult, sourceIt, recordCount, timedLogger, sourceIt);
        flushMissings(comparer, newresult, targetIt, recordCount, timedLogger, targetIt);

        return newresult;
    }

    private static <T> int flushMissings(ComparatorEquality<T> comparer, List<Mismatch<?>> newresult,
                                         Iterator<T> sourceIt, int recordCount, TimeBiDiscarter<String, Object[]> timedLogger, Iterator<T> it) {
        while (it.hasNext()) {
            addMissing(newresult, comparer, sourceIt.next());
            timedLogger.accept("Finalizing sources, {} records so far...", new Object[]{++recordCount});
        }
        return recordCount;
    }

}
