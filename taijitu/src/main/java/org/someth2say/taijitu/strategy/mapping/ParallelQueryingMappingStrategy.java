package org.someth2say.taijitu.strategy.mapping;


import org.apache.log4j.Logger;
import org.someth2say.taijitu.ComparisonRuntime;
import org.someth2say.taijitu.compare.ComparisonResult;
import org.someth2say.taijitu.tuple.ComparableTuple;
import org.someth2say.taijitu.config.ComparisonConfig;
import org.someth2say.taijitu.database.ResultSetIterator;

/**
 * Created by Jordi Sola on 16/02/2017.
 */
public class ParallelQueryingMappingStrategy extends AbstractMappingComparisonStrategy {

    public static final String NAME = "parallelMapping";
    private static final Logger logger = Logger.getLogger(ParallelQueryingMappingStrategy.class);
//
//    public void runComparison(final ComparisonRuntime comparison) throws TaijituException {
//
//        logger.info("Starting comparison for " + comparison.getTestName());
//
//        final ExceptionHoldingCyclicBarrier barrier = new ExceptionHoldingCyclicBarrier(2, comparison);
//        final SourceMappingQueryAction<ComparableTuple> sourceActions = new SourceMappingQueryAction<>(comparison, barrier);
//        final TargetMappingQueryAction<ComparableTuple> targetActions = new TargetMappingQueryAction<>(comparison, barrier);
//
//        Collection<Pair<Query, QueryActions<ComparableTuple>>> pairs = new ArrayList<>();
//        pairs.add(new ImmutablePair<>(comparison.getSource(), sourceActions));
//        pairs.add(new ImmutablePair<>(comparison.getTarget(), targetActions));
//        StrategyUtils.runParallelQueryActions(comparison, pairs);
//
//        logger.debug("Starting comparison for " + comparison.getTestName());
//
//        String[] fields = comparison.getFields();
//        String[] compareFields = comparison.getCompareFields();
//        Map<Class<?>, Comparator<Object>> comparators = comparison.getComparators();
//        SimpleComparisonResult result = comparison.getResult();
//        Map<Integer, ComparableTuple> sourceMap = sourceActions.getMappingResults().getMapValues();
//        Map<Integer, ComparableTuple> targetMap = targetActions.getMappingResults().getMapValues();
//        QueryMapperResultComparator.compareIntoResult(result, sourceMap, targetMap, fields, compareFields, comparators);
//
//        logger.info("Comparison complete for " + comparison.getTestName());
//
//    }


    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public <T extends ComparableTuple> ComparisonResult runComparison(ResultSetIterator<T> source, ResultSetIterator<T> target, ComparisonRuntime comparisonRuntime, ComparisonConfig comparisonConfig) {
        return null;
    }

//    /**
//     * @author Jordi Sola
//     */
//    static abstract class MappingQueryActions<T extends ComparableTuple> implements QueryActions<T> {
//
//        private final ExceptionHoldingCyclicBarrier barrier;
//        private final ComparisonRuntime comparison;
//        private int[] keyFieldsIdxs;
//        private Object[] keyValuesBuffer;
//        private QueryMapperResult<Integer, T> mappingResults;
//
//        public MappingQueryActions(ComparisonRuntime comparison, ExceptionHoldingCyclicBarrier barrier) {
//            this.comparison = comparison;
//            this.barrier = barrier;
//        }
//
//        @Override
//        public void start(String[] columnDescriptions) throws QueryActionsException {
//            mappingResults = new QueryMapperResult<>();
//
//            setColumnDescriptions(columnDescriptions);
//            // Mapping can not be done after both columnDescriptions are provided!
//            // So we need to wait for another thread to push the second description...
//            waitForAllDescriptionsToBeProvided();
//
//            // Once the comparison have been updated (by the barrier), we can prepare mapping parameters
//            prepareMappingParameters(columnDescriptions);
//        }
//
//        private void prepareMappingParameters(String[] columnDescriptions) {
//            String[] keyFields = comparison.getKeyFields();
//            this.keyFieldsIdxs = calculateKeyColumnIdxs(columnDescriptions, keyFields);
//            this.keyValuesBuffer = new Object[keyFields.length];
//        }
//
//        protected abstract void setColumnDescriptions(String[] columnDescriptions);
//
//
//        private void waitForAllDescriptionsToBeProvided() throws QueryActionsException {
//            try {
//                barrier.await();
//            } catch (InterruptedException | BrokenBarrierException e) {
//                throw new QueryActionsException("Problems while waiting for query results descriptions to be available!", e);
//            }
//        }
//
//        private int[] calculateKeyColumnIdxs(String[] columnDescriptions, String[] keyColumnNames) {
//            return ColumnDescriptionUtils.getFieldPositions(keyColumnNames, columnDescriptions);
//        }
//
//        @Override
//        public void step(T currentRecord) throws QueryActionsException {
//            QueryMapper.mapRow(keyFieldsIdxs, mappingResults, keyValuesBuffer, currentRecord);
//        }
//
//        @Override
//        public void end() throws QueryActionsException {
//            // Nothing to do
//        }
//
//        public QueryMapperResult<Integer, T> getMappingResults() {
//            return mappingResults;
//        }
//
//        protected SimpleComparisonResult getResult() {
//            return comparison.getResult();
//        }
//    }
//
//    static class SourceMappingQueryAction<T extends ComparableTuple> extends MappingQueryActions<T> {
//
//        public SourceMappingQueryAction(ComparisonRuntime comparison, ExceptionHoldingCyclicBarrier barrier) {
//            super(comparison, barrier);
//        }
//
//        @Override
//        protected void setColumnDescriptions(String[] columnDescriptions) {
//            getResult().setSourceColumnDescriptions(columnDescriptions);
//        }
//    }
//
//    static class TargetMappingQueryAction<T extends ComparableTuple> extends MappingQueryActions<T> {
//        public TargetMappingQueryAction(ComparisonRuntime comparison, ExceptionHoldingCyclicBarrier barrier) {
//            super(comparison, barrier);
//        }
//
//        @Override
//        protected void setColumnDescriptions(String[] columnDescriptions) {
//            getResult().setTargetColumnDescriptions(columnDescriptions);
//        }
//    }


}
