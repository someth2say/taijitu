package org.someth2say.taijitu.compare.equality.stream.sorted;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.compare.equality.composite.IComparableCompositeEquality;
import org.someth2say.taijitu.compare.equality.composite.ICompositeEquality;
import org.someth2say.taijitu.compare.result.ComparisonResult;
import org.someth2say.taijitu.compare.result.ComparisonResult.SourceIdAndStructure;
import org.someth2say.taijitu.compare.result.SimpleComparisonResult;
import org.someth2say.taijitu.config.interfaces.IStrategyCfg;
import org.someth2say.taijitu.compare.equality.stream.AbstractStreamEquality;

import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Created by Jordi Sola on 02/03/2017.
 */
public class ComparableStreamEquality<T> extends AbstractStreamEquality<T> {
    public static final String NAME = "sorted";
    private static final Logger logger = Logger.getLogger(ComparableStreamEquality.class);

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

            T sourceRecord = getNextRecordOrNull(source);
            T targetRecord = getNextRecordOrNull(target);
            
            while (sourceRecord != null && targetRecord != null) {

                int keyComparison = categorizer.compareTo(sourceRecord, targetRecord);
                if (keyComparison > 0) {
                    // SourceCfg is after target -> target record is not in source stream
                    result.addDisjoint(new SourceIdAndStructure<>(sourceId, targetRecord));
                    targetRecord = getNextRecordOrNull(target);
                } else if (keyComparison < 0) {
                    // SourceCfg is before target -> source record is not in target stream
                    result.addDisjoint(new SourceIdAndStructure<>(targetId, sourceRecord));
                    sourceRecord = getNextRecordOrNull(source);
                } else {
                    // same Keys
                    // TODO Consider more fine-grained value comparison result than a simple boolean
                    // (i.e. a set of different fields)
                    if (!getEquality().equals(sourceRecord, targetRecord)) {
                        // Records are different
                        result.addDifference(new SourceIdAndStructure<>(sourceId, sourceRecord),
                                new SourceIdAndStructure<>(targetId, targetRecord));
                    }
                    sourceRecord = getNextRecordOrNull(source);
                    targetRecord = getNextRecordOrNull(target);
                }
            }

            // At least, one stream is fully consumed, so add every other stream's element
            // to "missing"
            while (source.hasNext()) {
                result.addDisjoint(new SourceIdAndStructure<>(sourceId, source.next()));
            }
            while (target.hasNext()) {
                result.addDisjoint(new SourceIdAndStructure<>(targetId, target.next()));
            }

            return result;

        } else {
            logger.error("Sorted stream requires an IComparableCompositeEquality<T> categorizer (say, need to define category order)");
            return null;
        }
    }

    public static IStrategyCfg defaultConfig() {
        return () -> ComparableStreamEquality.NAME;
    }

}
