package org.someth2say.taijitu.strategy.sorted;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.ComparisonContext;
import org.someth2say.taijitu.compare.ComparisonResult;
import org.someth2say.taijitu.compare.SimpleComparisonResult;
import org.someth2say.taijitu.config.QuerySourceConfig;
import org.someth2say.taijitu.config.SourceConfig;
import org.someth2say.taijitu.source.Source;
import org.someth2say.taijitu.tuple.ComparableTuple;
import org.someth2say.taijitu.config.ComparisonConfig;
import org.someth2say.taijitu.config.StrategyConfig;
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
    public ComparisonResult runComparison(Source source, Source target, ComparisonContext comparisonContext, ComparisonConfig comparisonConfig) {
        final String comparisonName = comparisonConfig.getName();
        logger.debug("Start sorted strategy comparison for " + comparisonName);
        SimpleComparisonResult result = new SimpleComparisonResult(comparisonConfig);


        Iterator<ComparableTuple> sourceIterator = source.iterator();
        Iterator<ComparableTuple> targetIterator = target.iterator();
        ComparableTuple sourceRecord = getNextRecord(sourceIterator);
        ComparableTuple targetRecord = getNextRecord(targetIterator);

        final SourceConfig targetQuerySourceConfig = target.getConfig();
        final SourceConfig sourceQuerySourceConfig = source.getConfig();

        while (sourceRecord != null && targetRecord != null) {

            int keyComparison = sourceRecord.compareKeysTo(targetRecord);
            if (keyComparison > 0) {
                // Source is after target -> target record is not in source stream
                result.addDisjoint(new ComparisonResult.QueryAndTuple(targetQuerySourceConfig, targetRecord));
                targetRecord = getNextRecord(targetIterator);
            } else if (keyComparison < 0) {
                // Source is before target -> source record is not in target stream
                result.addDisjoint(new ComparisonResult.QueryAndTuple(sourceQuerySourceConfig, sourceRecord));
                sourceRecord = getNextRecord(sourceIterator);
            } else {
                // same Keys
                //TODO Consider more fine-grained value comparison result than a simple boolean (i.e. a set of different fields)
                if (!sourceRecord.equalsNonKeys(targetRecord)) {
                    // Records are different
                    result.addDifference(
                            new ComparisonResult.QueryAndTuple(sourceQuerySourceConfig, sourceRecord),
                            new ComparisonResult.QueryAndTuple(targetQuerySourceConfig, targetRecord));
                }
                sourceRecord = getNextRecord(sourceIterator);
                targetRecord = getNextRecord(targetIterator);
            }
        }

        //At least, one stream is fully consumed, so add every other stream's element to "missing"
        while (sourceIterator.hasNext()) {
            result.getDisjoint().add(new ComparisonResult.QueryAndTuple(sourceQuerySourceConfig, sourceIterator.next()));
        }
        while (targetIterator.hasNext()) {
            result.getDisjoint().add(new ComparisonResult.QueryAndTuple(targetQuerySourceConfig, targetIterator.next()));
        }

        return result;
    }


    public static StrategyConfig defaultConfig() {
        return () -> SortedStrategy.NAME;
    }
}
