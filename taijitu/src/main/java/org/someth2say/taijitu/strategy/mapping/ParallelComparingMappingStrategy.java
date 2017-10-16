package org.someth2say.taijitu.strategy.mapping;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.ComparisonRuntime;
import org.someth2say.taijitu.compare.ComparableTuple;
import org.someth2say.taijitu.compare.ComparisonResult;
import org.someth2say.taijitu.config.ComparisonConfig;
import org.someth2say.taijitu.database.ResultSetIterator;

/**
 * Created by Jordi Sola on 16/02/2017.
 */
public class ParallelComparingMappingStrategy extends AbstractMappingComparisonStrategy {

    public static final String NAME = "parallelComparing";
    private static final Logger logger = Logger.getLogger(ParallelComparingMappingStrategy.class);

//    public void runComparison(final ComparisonRuntime comparison) throws TaijituException {
//
//        logger.info("Starting comparison for " + comparison.getTestName());
//
//        final CyclicBarrier finalizationBarrier = new CyclicBarrier(2);
//        final ExceptionHoldingCyclicBarrier barrier = new ExceptionHoldingCyclicBarrier(2, comparison);
//
//        final SourceParallelComparingQueryAction sourceQueryAction = new SourceParallelComparingQueryAction(comparison, barrier, finalizationBarrier);
//        final TargetParallelComparingQueryAction targetQueryAction = new TargetParallelComparingQueryAction(comparison, barrier, finalizationBarrier);
//        sourceQueryAction.setOther(targetQueryAction);
//        targetQueryAction.setOther(sourceQueryAction);
//
//        Collection<Pair<Query, QueryActions<ComparableTuple>>> pairs = new ArrayList<>();
//        pairs.add(new ImmutablePair<>(comparison.getSource(), sourceQueryAction));
//        pairs.add(new ImmutablePair<>(comparison.getTarget(), targetQueryAction));
//        StrategyUtils.runParallelQueryActions(comparison, pairs);
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

//
//    /**
//     * @author Jordi Sola
//     */
//    static abstract class ParallelComparingQueryActions implements QueryActions<ComparableTuple> {
//
//        private final ExceptionHoldingCyclicBarrier descriptionBarrier;
//        private final CyclicBarrier finalizationBarrier;
//        private final ComparisonRuntime comparison;
//        protected int[] keyFieldsIdxs;
//        protected Object[] keyBuffer;
//        QueryMapperResult<Integer, ComparableTuple> mappingResults = new QueryMapperResult<>();
//        private ParallelComparingQueryActions other;
//        private int[] compareFieldsIdxs;
//
//        public ParallelComparingQueryActions(ComparisonRuntime comparison, ExceptionHoldingCyclicBarrier descriptionBarrier, CyclicBarrier finalizationBarrier) {
//            this.comparison = comparison;
//            this.descriptionBarrier = descriptionBarrier;
//            this.finalizationBarrier = finalizationBarrier;
//        }
//
//        @Override
//        public void start(String[] columnDescriptions) throws QueryActionsException {
//            setColumnDescriptions(columnDescriptions);
//            // Mapping can not be done after both columnDescriptions are provided!
//            // So we need to wait for another thread to push the second description...
//            awaitDescriptionBarrier();
//
//            // Once the comparison have been updated (by the descriptionBarrier), we can prepare mapping parameters
//            prepareParameters(columnDescriptions);
//
//            // We can not proceed 'til both threads have the parameters ready!
//            awaitFinalizationBarrier();
//
//        }
//
//        private void awaitDescriptionBarrier() throws QueryActionsException {
//            try {
//                descriptionBarrier.await();
//            } catch (InterruptedException | BrokenBarrierException e) {
//                throw new QueryActionsException("Problems while waiting for query results descriptions to be available!", e);
//            }
//        }
//
//        private void prepareParameters(String[] columnDescriptions) {
//            this.compareFieldsIdxs = ColumnDescriptionUtils.getFieldPositions(comparison.getCompareFields(), columnDescriptions);
//            this.keyFieldsIdxs = ColumnDescriptionUtils.getFieldPositions(comparison.getKeyFields(), columnDescriptions);
//            this.keyBuffer = new Object[comparison.getKeyFields().length];
//            initDifferent();
//        }
//
//        private void initDifferent() {
//            comparison.getResult().setDifferent(new ArrayList<>());
//        }
//
//        protected abstract void setColumnDescriptions(String[] columnDescriptions);
//
//
//        @Override
//        public void step(ComparableTuple currentRecord) throws QueryActionsException {
//            synchronized (comparison) {
//                if (isInOther(currentRecord)) {
//                    if (!currentRecordsAreEquals(currentRecord)) {
//                        logger.debug("Adding to diff" + currentRecord);
//                        addRecordToDiffs(currentRecord);
//                    }
//                    logger.debug("Removing " + currentRecord);
//                    removeFormOther(currentRecord);
//                } else {
//                    logger.debug("Adding " + currentRecord);
//                    addRecord(currentRecord);
//                }
//            }
//        }
//
//        protected abstract void addRecordToDiffs(ComparableTuple record);
//
//        private ComparableTuple removeFormOther(ComparableTuple currentRecordKey) {
//            return other.removeRecord(currentRecordKey);
//        }
//
//        private boolean isInOther(ComparableTuple record) {
//            return other.contains(record);
//        }
//
//        protected ComparableTuple removeRecord(ComparableTuple record) {
//            return mappingResults.getMapValues().remove(buildKey(record));
//        }
//
//        protected boolean contains(ComparableTuple record) {
//            return mappingResults.getMapValues().containsKey(buildKey(record));
//        }
//
//        private boolean currentRecordsAreEquals(ComparableTuple currentRecord) {
//            final ComparableTuple otherRecord = other.getRecord(currentRecord);
//            return currentRecord.equalsFields(otherRecord, comparison.getComparators(), this.compareFieldsIdxs);
//        }
//
//
//        @Override
//        public void end() throws QueryActionsException {
//            awaitFinalizationBarrier();
//            logger.debug("Finalizing comparison. Remaining " + mappingResults.getMapValues().values());
//            dumpMappingResults();
//        }
//
//        private void awaitFinalizationBarrier() throws QueryActionsException {
//            try {
//                finalizationBarrier.await();
//            } catch (InterruptedException | BrokenBarrierException e) {
//                throw new QueryActionsException("Problems while waiting for query results descriptions to be available!", e);
//            }
//        }
//
//
//        protected ComparisonResult getResult() {
//            return comparison.getResult();
//        }
//
//        protected ParallelComparingQueryActions getOther() {
//            return other;
//        }
//
//        public void setOther(ParallelComparingQueryActions other) {
//            this.other = other;
//        }
//
//        protected ComparableTuple getRecord(ComparableTuple record) {
//            return mappingResults.getMapValues().get(buildKey(record));
//        }
//
//
//        protected void addRecord(ComparableTuple record) {
//            QueryMapper.mapRow(keyFieldsIdxs, mappingResults, keyBuffer, record);
//        }
//
//
//        private int buildKey(ComparableTuple record) {
//            return QueryMapper.buildIntKey(record, keyFieldsIdxs, keyBuffer);
//        }
//
//        protected abstract void dumpMappingResults();
//
//
//    }
//
//    static class SourceParallelComparingQueryAction extends ParallelComparingQueryActions {
//
//        public SourceParallelComparingQueryAction(ComparisonRuntime comparison, ExceptionHoldingCyclicBarrier descriptionBarrier, CyclicBarrier finalizationBarrier) {
//            super(comparison, descriptionBarrier, finalizationBarrier);
//        }
//
//        @Override
//        protected void setColumnDescriptions(String[] columnDescriptions) {
//            getResult().setSourceColumnDescriptions(columnDescriptions);
//        }
//
//        @Override
//        protected void addRecordToDiffs(ComparableTuple record) {
//            ComparableTuple sourceObjects = record;
//            ComparableTuple targetObjects = getOther().getRecord(record);
//            getResult().getDifferent().add(new ImmutablePair<>(sourceObjects, targetObjects));
//        }
//
//        @Override
//        protected void dumpMappingResults() {
//            this.getResult().setSourceOnly(mappingResults.getMapValues().values());
//        }
//
//    }
//
//    static class TargetParallelComparingQueryAction extends ParallelComparingQueryActions {
//
//        public TargetParallelComparingQueryAction(ComparisonRuntime comparison, ExceptionHoldingCyclicBarrier descriptionBarrier, CyclicBarrier finalizationBarrier) {
//            super(comparison, descriptionBarrier, finalizationBarrier);
//        }
//
//        @Override
//        protected void setColumnDescriptions(String[] columnDescriptions) {
//            getResult().setTargetColumnDescriptions(columnDescriptions);
//        }
//
//        @Override
//        protected void dumpMappingResults() {
//            this.getResult().setTargetOnly(mappingResults.getMapValues().values());
//        }
//
//        @Override
//        protected void addRecordToDiffs(ComparableTuple record) {
//            ComparableTuple sourceObjects = getOther().getRecord(record);
//            ComparableTuple targetObjects = record;
//            getResult().getDifferent().add(new ImmutablePair<>(sourceObjects, targetObjects));
//        }
//    }
//

}
