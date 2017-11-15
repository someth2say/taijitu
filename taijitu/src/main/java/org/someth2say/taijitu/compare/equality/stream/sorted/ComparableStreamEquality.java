package org.someth2say.taijitu.compare.equality.stream.sorted;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.compare.equality.composite.IComparableCompositeEquality;
import org.someth2say.taijitu.compare.equality.composite.ICompositeEquality;
import org.someth2say.taijitu.compare.result.ComparisonResult;
import org.someth2say.taijitu.compare.result.ComparisonResult.SourceIdAndComposite;
import org.someth2say.taijitu.compare.result.SimpleComparisonResult;
import org.someth2say.taijitu.config.interfaces.IStrategyCfg;
import org.someth2say.taijitu.compare.equality.stream.AbstractStreamEquality;
import org.someth2say.taijitu.discarter.TimeBiDiscarter;

import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Created by Jordi Sola on 02/03/2017.
 */
public class ComparableStreamEquality<T> extends AbstractStreamEquality<T> {
    public static final String NAME = "sorted";
    private static final Logger logger = LoggerFactory.getLogger(ComparableStreamEquality.class);

    public ComparableStreamEquality(ICompositeEquality equality, ICompositeEquality categorizer) {
        super(equality, categorizer);
    }

    @Override
    public String getName() {
        return NAME;
    }

    public ComparisonResult<T> runComparison(Stream<T> source, Object sourceId, Stream<T> target, Object targetId) {
        return compare(source.iterator(), sourceId, target.iterator(), targetId);
    }

    private ComparisonResult<T> compare(Iterator<T> source, Object sourceId, Iterator<T> target, Object targetId) {
        if (getCategorizer() instanceof IComparableCompositeEquality) {
            IComparableCompositeEquality<T> categorizer = (IComparableCompositeEquality<T>) getCategorizer();

            SimpleComparisonResult<T> result = new SimpleComparisonResult<>();

            int recordCount = 0;
            T sourceRecord = getNextRecordOrNull(source);
            recordCount++;
            T targetRecord = getNextRecordOrNull(target);
            recordCount++;

            TimeBiDiscarter<String, Object[]> timedLogger = new TimeBiDiscarter<>(1000, logger::debug);
            while (sourceRecord != null && targetRecord != null) {
                //TODO: Use a TimeBiDiscarter to log progress here
                timedLogger.accept("Processing {} records so far...", new Object[]{recordCount});
                int keyComparison = categorizer.compareTo(sourceRecord, targetRecord);
                if (keyComparison > 0) {
                    // SourceCfg is after target -> target record is not in source stream
                    result.addDisjoint(new SourceIdAndComposite<>(sourceId, targetRecord));
                    targetRecord = getNextRecordOrNull(target);
                    recordCount++;
                } else if (keyComparison < 0) {
                    // SourceCfg is before target -> source record is not in target stream
                    result.addDisjoint(new SourceIdAndComposite<>(targetId, sourceRecord));
                    sourceRecord = getNextRecordOrNull(source);
                    recordCount++;
                } else {
                    // same Keys
                    // TODO Consider more fine-grained value comparison result than a simple boolean
                    // (i.e. a set of different fields)
                    if (!getEquality().equals(sourceRecord, targetRecord)) {
                        // Records are different
                        result.addDifference(new SourceIdAndComposite<>(sourceId, sourceRecord),
                                new SourceIdAndComposite<>(targetId, targetRecord));
                    }
                    sourceRecord = getNextRecordOrNull(source);
                    recordCount++;
                    targetRecord = getNextRecordOrNull(target);
                    recordCount++;
                }
            }

            // At least, one stream is fully consumed, so add every other stream's element
            // to "missing"
            while (source.hasNext()) {
                result.addDisjoint(new SourceIdAndComposite<>(sourceId, source.next()));
                timedLogger.accept("Finalizing source {}, {} records so far...", new Object[]{sourceId, ++recordCount});
            }
            while (target.hasNext()) {
                result.addDisjoint(new SourceIdAndComposite<>(targetId, target.next()));
                timedLogger.accept("Finalizing source {}, {} records so far...", new Object[]{targetId, ++recordCount});
            }

            return result;

        } else {
            logger.error("Sorted stream requires an IComparableCompositeEquality (say, need to define category order)");
            return null;
        }
    }

    public static IStrategyCfg defaultConfig() {
        return () -> ComparableStreamEquality.NAME;
    }

}
