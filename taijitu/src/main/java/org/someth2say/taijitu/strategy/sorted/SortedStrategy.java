package org.someth2say.taijitu.strategy.sorted;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.ComparisonContext;
import org.someth2say.taijitu.compare.ComparisonResult;
import org.someth2say.taijitu.compare.SimpleComparisonResult;
import org.someth2say.taijitu.source.Source;
import org.someth2say.taijitu.tuple.ComparableTuple;
import org.someth2say.taijitu.strategy.AbstractComparisonStrategy;
import org.someth2say.taijitu.strategy.ComparisonStrategy;

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
    public ComparisonResult runComparison(Source source, Source target, ComparisonContext comparisonContext, ComparisonConfigIface comparisonConfigIface) {
        final String comparisonName = comparisonConfigIface.getName();
        logger.debug("Start sorted strategy comparison for " + comparisonName);
        SimpleComparisonResult result = new SimpleComparisonResult(comparisonConfigIface);


        Iterator<ComparableTuple> sourceIterator = source.iterator();
        Iterator<ComparableTuple> targetIterator = target.iterator();
        ComparableTuple sourceRecord = getNextRecord(sourceIterator);
        ComparableTuple targetRecord = getNextRecord(targetIterator);

        final SourceConfigIface<SourceConfigIface> targetQuerySourceConfigIface = target.getConfig();
        final SourceConfigIface<SourceConfigIface> sourceQuerySourceConfigIface = source.getConfig();

        while (sourceRecord != null && targetRecord != null) {

            int keyComparison = sourceRecord.compareKeysTo(targetRecord);
            if (keyComparison > 0) {
                // Source is after target -> target record is not in source stream
                result.addDisjoint(new ComparisonResult.QueryAndTuple(targetQuerySourceConfigIface, targetRecord));
                targetRecord = getNextRecord(targetIterator);
            } else if (keyComparison < 0) {
                // Source is before target -> source record is not in target stream
                result.addDisjoint(new ComparisonResult.QueryAndTuple(sourceQuerySourceConfigIface, sourceRecord));
                sourceRecord = getNextRecord(sourceIterator);
            } else {
                // same Keys
                //TODO Consider more fine-grained value comparison result than a simple boolean (i.e. a set of different fields)
                if (!sourceRecord.equalsNonKeys(targetRecord)) {
                    // Records are different
                    result.addDifference(
                            new ComparisonResult.QueryAndTuple(sourceQuerySourceConfigIface, sourceRecord),
                            new ComparisonResult.QueryAndTuple(targetQuerySourceConfigIface, targetRecord));
                }
                sourceRecord = getNextRecord(sourceIterator);
                targetRecord = getNextRecord(targetIterator);
            }
        }

        //At least, one stream is fully consumed, so add every other stream's element to "missing"
        while (sourceIterator.hasNext()) {
            result.getDisjoint().add(new ComparisonResult.QueryAndTuple(sourceQuerySourceConfigIface, sourceIterator.next()));
        }
        while (targetIterator.hasNext()) {
            result.getDisjoint().add(new ComparisonResult.QueryAndTuple(targetQuerySourceConfigIface, targetIterator.next()));
        }

        return result;
    }


    public static StrategyConfigIface defaultConfig() {
        return () -> SortedStrategy.NAME;
    }
}
