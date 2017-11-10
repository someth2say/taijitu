package org.someth2say.taijitu.compare.equality.stream.sorted;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.compare.equality.structure.ComparableStructureEquality;
import org.someth2say.taijitu.compare.equality.structure.StructureEquality;
import org.someth2say.taijitu.compare.result.ComparisonResult;
import org.someth2say.taijitu.compare.result.ComparisonResult.SourceIdAndStructure;
import org.someth2say.taijitu.compare.result.SimpleComparisonResult;
import org.someth2say.taijitu.config.interfaces.IStrategyCfg;
import org.someth2say.taijitu.source.Source;
import org.someth2say.taijitu.compare.equality.stream.AbstractStreamEquality;

import java.util.Iterator;
import java.util.stream.Stream;

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

    public ComparisonResult<T> runComparison(Stream<T> source, Object sourceId, Stream<T> target, Object targetId) {
        return compare(source.iterator(), sourceId, target.iterator(), targetId);
    }

//    @Override
//    public ComparisonResult<T> runExternalComparison(Source<T> source, Source<T> target) {
//            Iterator<T> sourceIterator = source.iterator();
//            Iterator<T> targetIterator = target.iterator();
//            final Object sourceId = source.getConfig();
//            final Object targetId = target.getConfig();
//
//        return compare(sourceIterator, sourceId, targetIterator, targetId);
//    }

    private ComparisonResult<T> compare(Iterator<T> source, Object sourceId, Iterator<T> target, Object targetId) {
        if (getCategorizer() instanceof ComparableStructureEquality) {
            ComparableStructureEquality<T> categorizer = (ComparableStructureEquality<T>) getCategorizer();

            SimpleComparisonResult<T> result = new SimpleComparisonResult<>();

            T sourceRecord = getNextRecord(source);
            T targetRecord = getNextRecord(target);


            while (sourceRecord != null && targetRecord != null) {

                int keyComparison = categorizer.compareTo(sourceRecord, targetRecord);
                if (keyComparison > 0) {
                    // SourceCfg is after target -> target record is not in source stream
                    result.addDisjoint(new SourceIdAndStructure<>(sourceId, targetRecord));
                    targetRecord = getNextRecord(target);
                } else if (keyComparison < 0) {
                    // SourceCfg is before target -> source record is not in target stream
                    result.addDisjoint(new SourceIdAndStructure<>(targetId, sourceRecord));
                    sourceRecord = getNextRecord(source);
                } else {
                    // same Keys
                    // TODO Consider more fine-grained value comparison result than a simple boolean
                    // (i.e. a set of different fields)
                    if (!getEquality().equals(sourceRecord, targetRecord)) {
                        // Records are different
                        result.addDifference(new SourceIdAndStructure<>(sourceId, sourceRecord),
                                new SourceIdAndStructure<>(targetId, targetRecord));
                    }
                    sourceRecord = getNextRecord(source);
                    targetRecord = getNextRecord(target);
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
            logger.error("Sorted stream requires an ComparableStructureEquality<T> categorizer (say, need to define category order)");
            return null;
        }
    }

    public static IStrategyCfg defaultConfig() {
        return () -> ComparableStreamEquality.NAME;
    }

}
