package org.someth2say.taijitu.strategy.sorted;

import org.apache.log4j.Logger;
import org.apache.poi.ss.formula.functions.T;
import org.someth2say.taijitu.ComparisonRuntime;
import org.someth2say.taijitu.compare.ComparisonResult;
import org.someth2say.taijitu.compare.SimpleComparisonResult;
import org.someth2say.taijitu.config.QueryConfig;
import org.someth2say.taijitu.tuple.ComparableTuple;
import org.someth2say.taijitu.config.ComparisonConfig;
import org.someth2say.taijitu.config.StrategyConfig;
import org.someth2say.taijitu.database.ResultSetIterator;
import org.someth2say.taijitu.strategy.AbstractComparisonStrategy;
import org.someth2say.taijitu.strategy.ComparisonStrategy;
import org.someth2say.taijitu.util.ImmutablePair;

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
    public ComparisonResult runComparison(ResultSetIterator source, ResultSetIterator target, ComparisonRuntime comparisonRuntime, ComparisonConfig comparisonConfig) {
        final String comparisonName = comparisonConfig.getName();
        logger.debug("Start sorted strategy comparison for " + comparisonName);
        SimpleComparisonResult result = new SimpleComparisonResult(comparisonConfig);

        ComparableTuple sourceRecord = getNextRecord(source);
        ComparableTuple targetRecord = getNextRecord(target);

        final QueryConfig targetQueryConfig = comparisonConfig.getTargetQueryConfig();
        final QueryConfig sourceQueryConfig = comparisonConfig.getSourceQueryConfig();

        while (sourceRecord != null && targetRecord != null) {

            int keyComparison = sourceRecord.compareKeysTo(targetRecord);
            if (keyComparison > 0) {
                // Source is after target -> target record is not in source stream
                result.getDisjoint().add(new ComparisonResult.QueryAndTuple(targetQueryConfig, targetRecord));
                //result.getTargetOnly().add(targetRecord);
                targetRecord = getNextRecord(target);
            } else if (keyComparison < 0) {
                // Source is before target -> source record is not in target stream
                result.getDisjoint().add(new ComparisonResult.QueryAndTuple(sourceQueryConfig, sourceRecord));
                //result.getSourceOnly().add(sourceRecord);
                sourceRecord = getNextRecord(source);
            } else {
                // same Keys
                //TODO Consider more fine-grained value comparison result than a simple boolean (i.e. a set of different fields)
                if (!sourceRecord.equalsNonKeys(targetRecord)) {
                    // Records are different
                    result.getDifferent().add(new ImmutablePair<>(
                            new ComparisonResult.QueryAndTuple(sourceQueryConfig, sourceRecord),
                            new ComparisonResult.QueryAndTuple(targetQueryConfig, targetRecord)));
                }
                sourceRecord = getNextRecord(source);
                targetRecord = getNextRecord(target);
            }
        }

        //At least, one stream is fully consumed, so add every other stream's element to "missing"
        while (source.hasNext()) {
            result.getDisjoint().add(new ComparisonResult.QueryAndTuple(targetQueryConfig, targetRecord));
        }
        while (target.hasNext()) {
            result.getDisjoint().add(new ComparisonResult.QueryAndTuple(targetQueryConfig, target.next()));
        }

        return result;
    }


    public static StrategyConfig defaultConfig() {
        return () -> SortedStrategy.NAME;
    }
}
