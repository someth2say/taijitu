package org.someth2say.taijitu.compare.equality.stream.sorted;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.compare.equality.structure.ComparableStructureEquality;
import org.someth2say.taijitu.compare.equality.structure.StructureEquality;
import org.someth2say.taijitu.compare.result.ComparisonResult;
import org.someth2say.taijitu.compare.result.ComparisonResult.SourceAndTuple;
import org.someth2say.taijitu.compare.result.SimpleComparisonResult;
import org.someth2say.taijitu.config.interfaces.IStrategyCfg;
import org.someth2say.taijitu.source.Source;
import org.someth2say.taijitu.compare.equality.stream.AbstractStreamEquality;

import java.util.Iterator;

/**
 * Created by Jordi Sola on 02/03/2017.
 */
public class ComparableStreamEquality<T> extends AbstractStreamEquality<T> {
    public static final String NAME = "sorted";
    private static final Logger logger = Logger.getLogger(ComparableStreamEquality.class);

    public ComparableStreamEquality(StructureEquality equality, StructureEquality categorizer) {
        super(equality, categorizer);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public ComparisonResult<T> runExternalComparison(Source<T> source, Source<T> target) {
        if (getCategorizer() instanceof ComparableStructureEquality) {
            ComparableStructureEquality<T> categorizer = (ComparableStructureEquality<T>) getCategorizer();

            SimpleComparisonResult<T> result = new SimpleComparisonResult<>();

            Iterator<T> sourceIterator = source.iterator();
            Iterator<T> targetIterator = target.iterator();

            T sourceRecord = getNextRecord(sourceIterator);
            T targetRecord = getNextRecord(targetIterator);

            while (sourceRecord != null && targetRecord != null) {

                int keyComparison = categorizer.compareTo(sourceRecord, targetRecord);
                if (keyComparison > 0) {
                    // SourceCfg is after target -> target record is not in source stream
                    result.addDisjoint(new SourceAndTuple<>(source.getConfig(), targetRecord));
                    targetRecord = getNextRecord(targetIterator);
                } else if (keyComparison < 0) {
                    // SourceCfg is before target -> source record is not in target stream
                    result.addDisjoint(new SourceAndTuple<>(target.getConfig(), sourceRecord));
                    sourceRecord = getNextRecord(sourceIterator);
                } else {
                    // same Keys
                    // TODO Consider more fine-grained value comparison result than a simple boolean
                    // (i.e. a set of different fields)
                    if (!getEquality().equals(sourceRecord, targetRecord)) {
                        // Records are different
                        result.addDifference(new SourceAndTuple<>(source.getConfig(), sourceRecord),
                                new SourceAndTuple<>(target.getConfig(), targetRecord));
                    }
                    sourceRecord = getNextRecord(sourceIterator);
                    targetRecord = getNextRecord(targetIterator);
                }
            }

            // At least, one stream is fully consumed, so add every other stream's element
            // to "missing"
            while (sourceIterator.hasNext()) {
                result.addDisjoint(new SourceAndTuple<>(source.getConfig(), sourceIterator.next()));
            }
            while (targetIterator.hasNext()) {
                result.addDisjoint(new SourceAndTuple<>(target.getConfig(), targetIterator.next()));
            }

            return result;

        } else {
            logger.error("Sorted stream requires an ComparableStructureEquality<T> categorizer (say, need to define category order)");
            return null;
        }
    }

    public static IStrategyCfg defaultConfig() {
        return () -> ComparableStreamEquality.NAME;
    }

}
