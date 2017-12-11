package org.someth2say.taijitu.compare.equality.impl.stream.sorted;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.compare.equality.aspects.external.Comparator;
import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.compare.equality.impl.stream.StreamEqualizer;
import org.someth2say.taijitu.compare.result.Difference;
import org.someth2say.taijitu.compare.result.Unequal;
import org.someth2say.taijitu.compare.result.Missing;
import org.someth2say.taijitu.discarter.TimeBiDiscarter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Jordi Sola on 02/03/2017.
 */
public class ComparableStreamEqualizer<T> implements StreamEqualizer<T> {

    private static final Logger logger = LoggerFactory.getLogger(ComparableStreamEqualizer.class);
    private final Equalizer<T> equalizer;
    private final Comparator<T> categorizer;

    public ComparableStreamEqualizer(Equalizer<T> equalizer, Comparator<T> categorizer) {
        this.equalizer = equalizer;
        this.categorizer = categorizer;
    }

    @Override
    public List<Difference<?>> underlyingDiffs(Stream<T> source, Stream<T> target) {
        return compare(source, target, categorizer, equalizer);
    }

    public static <T> List<Difference<?>> compare(Stream<T> source, Stream<T> target, Comparator<T> comparator, Equalizer<T> equalizer) {
        List<Difference<?>> newresult = new ArrayList<>();
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
            int keyComparison = comparator.compare(sourceRecord, targetRecord);
            if (keyComparison > 0) {
                // SourceCfg is after target -> target record is not in source stream
                newresult.add(comparator.asMissing(targetRecord));
                targetRecord = getNextRecordOrNull(targetIt);
                recordCount++;
            } else if (keyComparison < 0) {
                // SourceCfg is before target -> source record is not in target stream
                newresult.add(comparator.asMissing(sourceRecord));
                sourceRecord = getNextRecordOrNull(sourceIt);
                recordCount++;
            } else {
                // same Keys
                Unequal<T> unequal = equalizer.asDifference(sourceRecord, targetRecord);
                if (unequal !=null) {
                    // Records are different
                    newresult.add(unequal);
                }
                sourceRecord = getNextRecordOrNull(sourceIt);
                recordCount++;
                targetRecord = getNextRecordOrNull(targetIt);
                recordCount++;
            }
        }

        // At least, one stream is fully consumed, so add every other stream's element
        // to "missing"
        recordCount = flushMissings(comparator, newresult, sourceIt, recordCount, timedLogger, sourceIt);
        flushMissings(comparator, newresult, targetIt, recordCount, timedLogger, targetIt);

        return newresult;
    }

    private static <T> int flushMissings(Comparator<T> comparator, List<Difference<?>> newresult,
                                         Iterator<T> sourceIt, int recordCount, TimeBiDiscarter<String, Object[]> timedLogger, Iterator<T> it) {
        while (it.hasNext()) {
            Missing<T> missing = comparator.asMissing(sourceIt.next());
            newresult.add(missing);
            timedLogger.accept("Finalizing sources, {} records so far...", new Object[]{++recordCount});
        }
        return recordCount;
    }

    private static <T> T getNextRecordOrNull(Iterator<T> resultSetSource) {
        return resultSetSource.hasNext() ? resultSetSource.next() : null;
    }
}
