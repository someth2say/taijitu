package org.someth2say.taijitu.strategy.sorted;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.ComparisonContext;
import org.someth2say.taijitu.compare.ComparisonResult;
import org.someth2say.taijitu.compare.ComparisonResult.SourceAndTuple;
import org.someth2say.taijitu.compare.SimpleComparisonResult;
import org.someth2say.taijitu.compare.equality.external.ExternalSortedEquality;
import org.someth2say.taijitu.compare.equality.external.ExternalEquality;
import org.someth2say.taijitu.config.interfaces.ISourceCfg;
import org.someth2say.taijitu.config.interfaces.IStrategyCfg;
import org.someth2say.taijitu.source.Source;
import org.someth2say.taijitu.strategy.AbstractComparisonStrategy;
import org.someth2say.taijitu.strategy.ComparisonStrategy;
import org.someth2say.taijitu.tuple.ComparableTuple;

import java.util.Iterator;

/**
 * Created by Jordi Sola on 02/03/2017.
 */
public class SortedStrategy extends AbstractComparisonStrategy implements ComparisonStrategy {
    public static final String NAME = "sorted";
    private static final Logger logger = Logger.getLogger(SortedStrategy.class);

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public <T extends ComparableTuple> ComparisonResult runComparison(Source<T> source, Source<T> target, ComparisonContext comparisonContext){
//        final String comparisonName = iComparisonCfg.getName();
//        logger.debug("Start sorted strategy comparison for " + comparisonName);
        SimpleComparisonResult result = new SimpleComparisonResult();

        Iterator<T> sourceIterator = source.iterator();
        Iterator<T> targetIterator = target.iterator();
        ComparableTuple sourceRecord = getNextRecord(sourceIterator);
        ComparableTuple targetRecord = getNextRecord(targetIterator);

        final ISourceCfg targetSrcConfig = target.getConfig();
        final ISourceCfg sourceSrcConfig = source.getConfig();

        while (sourceRecord != null && targetRecord != null) {

            int keyComparison = sourceRecord.compareKeysTo(targetRecord);
            if (keyComparison > 0) {
                // SourceCfg is after target -> target record is not in source stream
                result.addDisjoint(new SourceAndTuple<>(targetSrcConfig, targetRecord));
                targetRecord = getNextRecord(targetIterator);
            } else if (keyComparison < 0) {
                // SourceCfg is before target -> source record is not in target stream
                result.addDisjoint(new SourceAndTuple<>(sourceSrcConfig, sourceRecord));
                sourceRecord = getNextRecord(sourceIterator);
            } else {
                // same Keys
                // TODO Consider more fine-grained value comparison result than a simple boolean
                // (i.e. a set of different fields)
                if (!sourceRecord.equalsNonKeys(targetRecord)) {
                    // Records are different
                    result.addDifference(new SourceAndTuple<>(sourceSrcConfig, sourceRecord),
                            new SourceAndTuple<>(targetSrcConfig, targetRecord));
                }
                sourceRecord = getNextRecord(sourceIterator);
                targetRecord = getNextRecord(targetIterator);
            }
        }

        // At least, one stream is fully consumed, so add every other stream's element
        // to "missing"
        while (sourceIterator.hasNext()) {
            result.addDisjoint(new SourceAndTuple<>(sourceSrcConfig, sourceIterator.next()));
        }
        while (targetIterator.hasNext()) {
            result.addDisjoint(new SourceAndTuple<>(targetSrcConfig, targetIterator.next()));
        }

        return result;
    }

    @Override
    public <T> ComparisonResult runExternalComparison(Source<T> source, Source<T> target, ExternalEquality<T> externalCategorizer, ExternalEquality<T> equality) {
        if (externalCategorizer instanceof ExternalSortedEquality) {
            ExternalSortedEquality<T> categorizer = (ExternalSortedEquality<T>) externalCategorizer;

            SimpleComparisonResult result = new SimpleComparisonResult();

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
                    if (!equality.equals(sourceRecord, targetRecord)) {
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
            logger.error("Sorted strategy requires an ExternalSortedEquality<T> categorizer (say, need to define category order)");
            return null;
        }
    }

    public static IStrategyCfg defaultConfig() {
        return () -> SortedStrategy.NAME;
    }
}
