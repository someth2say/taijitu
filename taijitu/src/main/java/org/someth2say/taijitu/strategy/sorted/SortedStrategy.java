package org.someth2say.taijitu.strategy.sorted;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.ComparisonRuntime;
import org.someth2say.taijitu.compare.ComparableTuple;
import org.someth2say.taijitu.compare.ComparisonResult;
import org.someth2say.taijitu.config.ComparisonConfig;
import org.someth2say.taijitu.config.QueryConfig;
import org.someth2say.taijitu.database.ResultSetIterator;
import org.someth2say.taijitu.tuple.Tuple;
import org.someth2say.taijitu.strategy.ComparisonStrategy;
import org.someth2say.taijitu.util.ImmutablePair;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jordi Sola on 02/03/2017.
 */
public class SortedStrategy implements ComparisonStrategy {
    public static final String NAME = "sorted";
    private static final Logger logger = Logger.getLogger(SortedStrategy.class);

    //TODO: This is common to all comparison strategies, so should be moved to an abstract superclass
    private Map<String, Object[]> keyBuffers = new HashMap<>(2);

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public <T extends ComparableTuple> ComparisonResult runComparison(ResultSetIterator<T> source, ResultSetIterator<T> target, ComparisonRuntime comparisonRuntime, ComparisonConfig comparisonConfig) {
        final String comparisonName = comparisonConfig.getName();
        logger.debug("Start sorted strategy comparison for " + comparisonName);
        ComparisonResult result = new ComparisonResult(comparisonConfig);

        int[] keyColumnsIdxs = comparisonRuntime.getKeyColumnsIdxs();
        int[] nonKeyColumnsIdxs = comparisonRuntime.getNonKeyColumnsIdxs();

        T sourceRecord = getNextRecord(source);
        T targetRecord = getNextRecord(target);

        while (sourceRecord != null && targetRecord != null) {

            final Object[] sourceKey = buildKey(sourceRecord, comparisonConfig.getSourceQueryConfig(), keyColumnsIdxs);
            final Object[] targetKey = buildKey(targetRecord, comparisonConfig.getTargetQueryConfig(), keyColumnsIdxs);

            int keyComparison = compareKeys(sourceKey, targetKey);
            if (keyComparison > 0) {
                // Source is after target -> target record is not in source stream
                result.getTargetOnly().add(targetRecord);
                targetRecord = getNextRecord(target);
            } else if (keyComparison < 0) {
                // Source is before target -> source record is not in target stream
                result.getSourceOnly().add(sourceRecord);
                sourceRecord = getNextRecord(source);
            } else {
                // same Keys
                //TODO Consider more fine-grained value comparison result than a simple boolean (i.e. a set of different fields)
                boolean valueComparison = equalsRecordValues(sourceRecord, targetRecord, comparisonRuntime, comparisonConfig, nonKeyColumnsIdxs);

                if (!valueComparison) {
                    // Records are different
                    result.getDifferent().add(new ImmutablePair<>(sourceRecord, targetRecord));
                }
                sourceRecord = getNextRecord(source);
                targetRecord = getNextRecord(target);
            }
        }

        //At least, one stream is fully consumed, so add every other stream's element to "missing"
        while (source.hasNext()) {
            result.getSourceOnly().add(source.next());
        }
        while (target.hasNext()) {
            result.getTargetOnly().add(target.next());
        }

        return result;
    }

    private <T extends ComparableTuple> boolean equalsRecordValues(T sourceRecord, T targetRecord, ComparisonRuntime comparisonRuntime, ComparisonConfig comparisonConfig, int[] nonKeyColumnsIdxs) {
        // Things to have in mind:
        // - Do not forget comparators! Each column may have its own comparator... how we do map that?
        for (int nonKeyColumnIdx : nonKeyColumnsIdxs) {
            if (!equalColumnValue(nonKeyColumnIdx, sourceRecord, targetRecord, comparisonRuntime, comparisonConfig)) {
                return false;
            }
        }
        return true;
    }

    private <T extends ComparableTuple> boolean equalColumnValue(int nonKeyColumn, T sourceRecord, T targetRecord, ComparisonRuntime comparisonRuntime, ComparisonConfig comparisonConfig) {

        final Object sourceValue = sourceRecord.getValue(nonKeyColumn);
        final Object targetValue = targetRecord.getValue(nonKeyColumn);

        // We assume both values are the same type, and use 'source' as base.
        final Comparator<Object> comparator = comparisonRuntime.getComparatorForColumn(sourceValue.getClass(), comparisonConfig);
        return comparator.compare(sourceValue, targetValue) == 0;
    }

    private <T extends Tuple> T getNextRecord(ResultSetIterator<T> source) {
        return source.hasNext() ? source.next() : null;
    }

    private <T extends Tuple> Object[] buildKey(T record, QueryConfig queryConfig, final int[] keyFieldsIdxs) {
        Object[] keyBuffer = getKeyBuffer(queryConfig);
        buildKey(record, keyFieldsIdxs, keyBuffer);
        return keyBuffer;
    }

    private static <T extends Tuple> void buildKey(T row, int[] keyColumnIdxs, Object[] keyValuesBuffer) {
        int keyIdx = 0;
        for (int keyColumnIdx : keyColumnIdxs) {
            keyValuesBuffer[keyIdx++] = row.getValue(keyColumnIdx);
        }
    }
    private Object[] getKeyBuffer(QueryConfig queryConfig) {
       return keyBuffers.computeIfAbsent(queryConfig.getName(), s -> new Object[queryConfig.getKeyFields().length]);
    }


    private int compareKeys(Object[] sourceKey, Object[] targetKey) {
        for (int keyFieldIdx = 0; keyFieldIdx < sourceKey.length; keyFieldIdx++) {
            //TODO: Should use comparators!
            int keyComparison = sourceKey[keyFieldIdx].toString().compareTo(targetKey[keyFieldIdx].toString());
            if (keyComparison != 0)
                return keyComparison;
        }
        return 0;
    }

}
