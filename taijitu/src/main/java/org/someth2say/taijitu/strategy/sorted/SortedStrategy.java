package org.someth2say.taijitu.strategy.sorted;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.compare.ComparableTuple;
import org.someth2say.taijitu.config.ComparisonConfig;
import org.someth2say.taijitu.config.QueryConfig;
import org.someth2say.taijitu.query.ResultSetIterator;
import org.someth2say.taijitu.query.columnDescription.ColumnDescriptionUtils;
import org.someth2say.taijitu.query.tuple.Tuple;
import org.someth2say.taijitu.query.queryactions.QueryActions;
import org.someth2say.taijitu.query.queryactions.QueryActionsException;
import org.someth2say.taijitu.strategy.ComparisonStrategy;
import org.someth2say.taijitu.strategy.ExceptionHoldingCyclicBarrier;
import org.someth2say.taijitu.strategy.ExceptionHoldingRunnable;
import org.someth2say.taijitu.strategy.mapping.ParallelComparingMappingStrategy;
import org.someth2say.taijitu.ComparisonRuntime;
import org.someth2say.taijitu.compare.ComparisonResult;
import org.someth2say.taijitu.util.ImmutablePair;
import org.someth2say.taijitu.util.Pair;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jordi Sola on 02/03/2017.
 */
public class SortedStrategy implements ComparisonStrategy {
    public static final String NAME = "sorted";
    private static final Logger logger = Logger.getLogger(ParallelComparingMappingStrategy.class);

    //TODO: This is common to all comparison strategies, so should be moved to an abstract superclass
    private Map<String, Object[]> keyBuffers = new HashMap<>(2);
    private Map<String, int[]> keyFieldsIdxsBuffers = new HashMap<>(2);

    @Override
    public String getName() {
        return NAME;
    }

//    @SuppressWarnings("unchecked")
//    @Override
//    public void runComparison(final ComparisonRuntime taijituData, final ComparisonConfig comparisonConfig) throws TaijituException {
//
//        logger.info("Starting comparison for " + comparison.getTestName());
//
//        final ExceptionHoldingCyclicBarrier barrier = new ExceptionHoldingCyclicBarrier(2, comparison);
//        final SourceQueueingQueryActions sourceQueueingQueryActions = new SourceQueueingQueryActions(comparison,
//                barrier);
//        final TargetQueueingQueryActions targetQueueingQueryActions = new TargetQueueingQueryActions(comparison,
//                barrier);
//
//        Collection<Pair<Query, QueryActions<ComparableTuple>>> pairs = new ArrayList<>();
//        pairs.add(new ImmutablePair<>(comparison.getSource(), sourceQueueingQueryActions));
//        pairs.add(new ImmutablePair<>(comparison.getTarget(), targetQueueingQueryActions));
//        StrategyUtils.runParallelQueryActions(comparison, pairs,
//                new SortedComparer(comparison, sourceQueueingQueryActions, targetQueueingQueryActions));
//
//        logger.info("Comparison complete for " + comparison.getTestName());
//
//    }

    @Override
    public <T extends ComparableTuple> ComparisonResult runComparison(ResultSetIterator<T> source, ResultSetIterator<T> target, ComparisonRuntime comparisonRuntime, ComparisonConfig comparisonConfig) {

        int[] sourceKeyFieldsIdxs = comparisonRuntime.getSourceKeyFieldsIdxs(comparisonConfig.getSourceQueryConfig(), comparisonRuntime.getCanonicalColumns());
        int[] targetKeyFieldsIdxs = comparisonRuntime.getTargetKeyFieldsIdxs(comparisonConfig.getTargetQueryConfig(), comparisonRuntime.getCanonicalColumns());

        logger.debug("Start sorted stratey comparison for " + comparisonConfig.getName());
        ComparisonResult result = new ComparisonResult(comparisonConfig);

        T sourceRecord = getNextRecord(source);
        T targetRecord = getNextRecord(target);
        while (sourceRecord != null && targetRecord != null) {

            final Object[] sourceKey = buildKey(sourceRecord, comparisonConfig.getSourceQueryConfig(), sourceKeyFieldsIdxs);
            final Object[] targetKey = buildKey(targetRecord, comparisonConfig.getTargetQueryConfig(), targetKeyFieldsIdxs);

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
                //TODO Consider more fine-grained value comparison result than a simple boolean (i.e. a set of differen fields)
                boolean valueComparison = equalRecordValues(sourceRecord, targetRecord, comparisonRuntime, comparisonConfig);

                if (!valueComparison) {
                    // Records are different
                    result.getDifferent().add(new ImmutablePair<>(sourceObjects, targetObjects));
                }
                sourceRecord = getNextRecord(source);
                targetRecord = getNextRecord(target);
            }
        }

        //At least, one stream is fully consumed, so add every other stream's element to "missing"
        while (source.hasNext()) {
            getResult().getSourceOnly().add(source.next());
        }
        while (target.hasNext()) {
            getResult().getTargetOnly().add(target.next());
        }
    }

    private <T extends ComparableTuple> boolean equalRecordValues(T sourceRecord, T targetRecord, ComparisonRuntime comparisonRuntime, ComparisonConfig comparisonConfig) {
        // Things to have in mind:
        // - Records are already "the same", so we already know key fields are equals. Need to skip those fields.
        // - Key fields need not to be in the same column index...
        // - Optionally, it may exist a "map" between source and target columns... but positional will be much faster
        // - Do not forget comparators! Each column may have its own comparator... how we do map that?
        // IDEA: Define columnMatchingStrategies
        //   - Positional: Matching columns on the same position.
        //   - By name: Matching columns with the same name (
        String[] nonKeyColumns = comparisonRuntime.getNonKeyColumns();
        for (String nonKeyColumn : nonKeyColumns) {
            if (!equalColumnValue(nonKeyColumn, sourceRecord, targetRecord, comparisonRuntime, comparisonConfig)) {
                return false;
            }
        }
        return true;

    }

    private <T extends ComparableTuple> boolean equalColumnValue(String nonKeyColumn, T sourceRecord, T targetRecord, ComparisonRuntime comparisonRuntime, ComparisonConfig comparisonConfig) {
        int sourceColumnPosition = comparisonRuntime.getColumnPosition(nonKeyColumn);
        int targetColumnPosition = comparisonRuntime.getColumnMatchingStrategy().getTargetColumnPosition(nonKeyColumn);
        final Object sourceValue = sourceRecord.getValue(sourceColumnPosition);
        final Object targetValue = targetRecord.getValue(targetColumnPosition);

        // We assume both values are the same type, and use 'source' as base.
        final Comparator<Object> comparator = comparisonRuntime.getComparatorForColumn(nonKeyColumn, sourceValue.getClass(), comparisonConfig);
        return comparator.compare(sourceValue, targetValue) == 0;
    }


    private <T extends Tuple> T getNextRecord(ResultSetIterator<T> source) {
        return source.hasNext() ? source.next() : null;
    }

    private <T extends Tuple> Object[] buildKey(T record, QueryConfig queryConfig, final int[] keyFieldsIdxs) {
        Object[] keyBuffer = getKeyBuffer(queryConfig);
        ColumnDescriptionUtils.buildKey(record, keyFieldsIdxs, keyBuffer);
        return keyBuffer;
    }

    private int[] getKeyFieldsIdxs(final QueryConfig queryConfig, final String[] queryColumns) {
        int[] result = keyFieldsIdxsBuffers.get(queryConfig.getName());
        if (result == null) {
            result = buildKeyFieldsIdxs(queryConfig, queryColumns);
            keyFieldsIdxsBuffers.put(queryConfig.getName(), result);
        }
        return result;
    }

    private int[] buildKeyFieldsIdxs(final QueryConfig queryConfig, final String[] queryColumns) {
        String[] keyFields = queryConfig.getKeyFields();
        return ColumnDescriptionUtils.getFieldPositions(keyFields, queryColumns);
    }

    private Object[] getKeyBuffer(QueryConfig queryConfig) {
        //TODO: Consider using queryConfig as the key, instead of the name (implies computing equals/hashcode)
        Object[] result = keyBuffers.get(queryConfig.getName());
        if (result == null) {
            result = buildKeyBuffer(queryConfig);
            keyBuffers.put(queryConfig.getName(), result);
        }
        return result;
    }

    private Object[] buildKeyBuffer(QueryConfig queryConfig) {
        String[] keyFields = queryConfig.getKeyFields();
        return new Object[keyFields.length];
    }


    private int compareKeys(Object[] sourceKey, Object[] targetKey) {
        for (int keyFieldIdx = 0; keyFieldIdx < sourceKey.length; keyFieldIdx++) {
            int keyComparison = sourceKey[keyFieldIdx].toString().compareTo(targetKey[keyFieldIdx].toString());
            if (keyComparison != 0)
                return keyComparison;
        }
        return 0;
    }


    private abstract class QueueingQueryActions implements QueryActions<ComparableTuple> {
        private static final int DEFAULT_BUFFER_SIZE = 4;
        protected final ComparisonRuntime comparison;
        private final ExceptionHoldingCyclicBarrier barrier;

        BlockingQueue<Pair<Object[], ComparableTuple>> queue = new ArrayBlockingQueue<>(DEFAULT_BUFFER_SIZE);

        private boolean exhausted;

        private int[] compareFieldsIdxs;
        private int[] keyFieldsIdxs;
        private Object[] keyBuffer;

        public QueueingQueryActions(ComparisonRuntime comparison, ExceptionHoldingCyclicBarrier barrier) {
            this.comparison = comparison;
            this.barrier = barrier;
        }

        @Override
        public void start(String[] columnDescriptions) throws QueryActionsException {
            setColumnDescriptions(columnDescriptions);
            waitForAllDescriptionsToBeProvided();
            prepareParameters(columnDescriptions);
        }

        protected abstract void setColumnDescriptions(String[] columnDescriptions);

        private void prepareParameters(String[] columnDescriptions) {
            this.compareFieldsIdxs = ColumnDescriptionUtils.getFieldPositions(comparison.getCompareFields(),
                    columnDescriptions);
            this.keyFieldsIdxs = ColumnDescriptionUtils.getFieldPositions(comparison.getKeyFields(),
                    columnDescriptions);
            this.keyBuffer = new Object[comparison.getKeyFields().length];
        }

        private void waitForAllDescriptionsToBeProvided() throws QueryActionsException {
            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new QueryActionsException(
                        "Problems while waiting for query results descriptions to be available!", e);
            }
        }

        @Override
        public void step(ComparableTuple record) throws QueryActionsException {
            try {
                queue.put(new ImmutablePair<>(getKey(record), record));
            } catch (InterruptedException e) {
                throw new QueryActionsException("Interrupted while stepping", e);
            }
        }

        private Object[] getKey(ComparableTuple sourceRecord) {
            ColumnDescriptionUtils.buildKey(sourceRecord, keyFieldsIdxs, keyBuffer);
            return keyBuffer.clone();
        }

        @Override
        public void end() throws QueryActionsException {
            exhausted = true;
        }

        public boolean isExhausted() {
            return exhausted;
        }

        protected ComparisonResult getResult() {
            return getComparison().getResult();
        }

        private ComparisonRuntime getComparison() {
            return comparison;
        }
    }

    private class SourceQueueingQueryActions extends QueueingQueryActions {

        public SourceQueueingQueryActions(ComparisonRuntime comparison, ExceptionHoldingCyclicBarrier barrier) {
            super(comparison, barrier);
        }

        @Override
        protected void setColumnDescriptions(String[] columnDescriptions) {
            getResult().setSourceColumnDescriptions(columnDescriptions);
        }

    }

    private class TargetQueueingQueryActions extends QueueingQueryActions {

        public TargetQueueingQueryActions(ComparisonRuntime comparison, ExceptionHoldingCyclicBarrier barrier) {
            super(comparison, barrier);
        }

        @Override
        protected void setColumnDescriptions(String[] columnDescriptions) {
            getResult().setTargetColumnDescriptions(columnDescriptions);
        }
    }

    private class SortedComparer implements ExceptionHoldingRunnable<InterruptedException> {
        private final ComparisonRuntime comparison;
        private final QueueingQueryActions source;
        private final QueueingQueryActions target;
        private InterruptedException exception;

        public SortedComparer(ComparisonRuntime comparison, QueueingQueryActions source, QueueingQueryActions target) {
            this.comparison = comparison;
            this.source = source;
            this.target = target;
        }

        private ComparisonResult getResult() {
            return getComparison().getResult();
        }

        private ComparisonRuntime getComparison() {
            return comparison;
        }

        @Override
        public void run() {
            logger.debug("Started sorted consumer thread.");
            initResults();
            try {
                Pair<Object[], ComparableTuple> sourceRecord = getNextRecord(source);
                Pair<Object[], ComparableTuple> targetRecord = getNextRecord(target);

                while (sourceRecord != null || targetRecord != null) {
                    if (sourceRecord != null && targetRecord != null) {
                        final ComparableTuple sourceObjects = sourceRecord.getValue();
                        final ComparableTuple targetObjects = targetRecord.getValue();
                        final Object[] sourceKey = sourceRecord.getKey();
                        final Object[] targetKey = targetRecord.getKey();
                        int keyComparison = compareKeys(sourceKey, targetKey);
                        if (keyComparison == 0) {
                            // same Keys
                            if (!sourceObjects.equalsFields(targetObjects, comparison.getComparators(),
                                    source.compareFieldsIdxs)) {
                                // Records are different
                                getResult().getDifferent().add(new ImmutablePair<>(sourceObjects, targetObjects));
                            }
                            sourceRecord = getNextRecord(source);
                            targetRecord = getNextRecord(target);
                        } else if (keyComparison > 0) {
                            // Source is after target
                            getResult().getTargetOnly().add(targetObjects);
                            targetRecord = getNextRecord(this.target);
                        } else if (keyComparison < 0) {
                            // Source is before target
                            getResult().getSourceOnly().add(sourceObjects);
                            sourceRecord = getNextRecord(source);
                        }
                    } else if (sourceRecord != null) { // && targetRecord == null) {
                        getResult().getSourceOnly().add(sourceRecord.getValue());
                        sourceRecord = getNextRecord(source);
                    } else { // if (targetRecord != null && sourceRecord == null) {
                        getResult().getTargetOnly().add(targetRecord.getValue());
                        targetRecord = getNextRecord(target);
                    }
                }
            } catch (InterruptedException e) {
                exception = e;
            }
            logger.debug("Finalizing sorted consumer thread.");

        }

        private void initResults() {
            getResult().setDifferent(new ArrayList<>());
            getResult().setSourceOnly(new ArrayList<>());
            getResult().setTargetOnly(new ArrayList<>());
        }

        private int compareKeys(Object[] sourceKey, Object[] targetKey) {
            for (int keyFieldIdx = 0; keyFieldIdx < sourceKey.length; keyFieldIdx++) {
                int keyComparison = sourceKey[keyFieldIdx].toString().compareTo(targetKey[keyFieldIdx].toString());
                if (keyComparison != 0)
                    return keyComparison;
            }
            return 0;
        }

        private Pair<Object[], ComparableTuple> getNextRecord(QueueingQueryActions actions)
                throws InterruptedException {
            if (actions.isExhausted() && actions.queue.isEmpty())
                return null;
            Pair<Object[], ComparableTuple> objects = actions.queue.poll(100, TimeUnit.MILLISECONDS);
            while (objects == null && !actions.isExhausted()) {
                objects = actions.queue.poll(100, TimeUnit.MILLISECONDS);
            }
            return objects;
        }

        @Override
        public InterruptedException getException() {
            return exception;
        }
    }
}
