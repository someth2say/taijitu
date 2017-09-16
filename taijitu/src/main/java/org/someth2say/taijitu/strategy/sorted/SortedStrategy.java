package org.someth2say.taijitu.strategy.sorted;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.query.Query;
import org.someth2say.taijitu.query.columnDescription.ColumnDescriptionUtils;
import org.someth2say.taijitu.query.queryactions.QueryActions;
import org.someth2say.taijitu.query.queryactions.QueryActionsException;
import org.someth2say.taijitu.strategy.ComparisonStrategy;
import org.someth2say.taijitu.strategy.ExceptionHoldingCyclicBarrier;
import org.someth2say.taijitu.strategy.ExceptionHoldingRunnable;
import org.someth2say.taijitu.strategy.StrategyUtils;
import org.someth2say.taijitu.strategy.mapping.ParallelComparingMappingStrategy;
import org.someth2say.taijitu.TaijituException;
import org.someth2say.taijitu.TaijituData;
import org.someth2say.taijitu.compare.ComparableObjectArray;
import org.someth2say.taijitu.compare.ComparisonResult;
import org.someth2say.taijitu.util.ImmutablePair;
import org.someth2say.taijitu.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
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

	@Override
	public String getName() {
		return NAME;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void runComparison(TaijituData comparison) throws TaijituException {

		logger.info("Starting comparison for " + comparison.getTestName());

		final ExceptionHoldingCyclicBarrier barrier = new ExceptionHoldingCyclicBarrier(2, comparison);
		final SourceQueueingQueryActions sourceQueueingQueryActions = new SourceQueueingQueryActions(comparison,
				barrier);
		final TargetQueueingQueryActions targetQueueingQueryActions = new TargetQueueingQueryActions(comparison,
				barrier);

		Collection<Pair<Query, QueryActions<ComparableObjectArray>>> pairs = new ArrayList<>();
		pairs.add(new ImmutablePair<>(comparison.getSource(), sourceQueueingQueryActions));
		pairs.add(new ImmutablePair<>(comparison.getTarget(), targetQueueingQueryActions));
		StrategyUtils.runParallelQueryActions(comparison, pairs,
				new SortedComparer(comparison, sourceQueueingQueryActions, targetQueueingQueryActions));

		logger.info("Comparison complete for " + comparison.getTestName());

	}

	private abstract class QueueingQueryActions implements QueryActions<ComparableObjectArray> {
		private static final int DEFAULT_BUFFER_SIZE = 4;
		protected final TaijituData comparison;
		private final ExceptionHoldingCyclicBarrier barrier;

		BlockingQueue<Pair<Object[], ComparableObjectArray>> queue = new ArrayBlockingQueue<>(DEFAULT_BUFFER_SIZE);

		private boolean exhausted;

		private int[] compareFieldsIdxs;
		private int[] keyFieldsIdxs;
		private Object[] keyBuffer;

		public QueueingQueryActions(TaijituData comparison, ExceptionHoldingCyclicBarrier barrier) {
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
		public void step(ComparableObjectArray record) throws QueryActionsException {
			try {
				queue.put(new ImmutablePair<>(getKey(record), record));
			} catch (InterruptedException e) {
				throw new QueryActionsException("Interrupted while stepping", e);
			}
		}

		private Object[] getKey(ComparableObjectArray sourceRecord) {
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

		private TaijituData getComparison() {
			return comparison;
		}
	}

	private class SourceQueueingQueryActions extends QueueingQueryActions {

		public SourceQueueingQueryActions(TaijituData comparison, ExceptionHoldingCyclicBarrier barrier) {
			super(comparison, barrier);
		}

		@Override
		protected void setColumnDescriptions(String[] columnDescriptions) {
			getResult().setSourceColumnDescriptions(columnDescriptions);
		}

	}

	private class TargetQueueingQueryActions extends QueueingQueryActions {

		public TargetQueueingQueryActions(TaijituData comparison, ExceptionHoldingCyclicBarrier barrier) {
			super(comparison, barrier);
		}

		@Override
		protected void setColumnDescriptions(String[] columnDescriptions) {
			getResult().setTargetColumnDescriptions(columnDescriptions);
		}
	}

	private class SortedComparer implements ExceptionHoldingRunnable<InterruptedException> {
		private final TaijituData comparison;
		private final QueueingQueryActions source;
		private final QueueingQueryActions target;
		private InterruptedException exception;

		public SortedComparer(TaijituData comparison, QueueingQueryActions source, QueueingQueryActions target) {
			this.comparison = comparison;
			this.source = source;
			this.target = target;
		}

		private ComparisonResult getResult() {
			return getComparison().getResult();
		}

		private TaijituData getComparison() {
			return comparison;
		}

		@Override
		public void run() {
			logger.debug("Started sorted consumer thread.");
			initResults();
			try {
				Pair<Object[], ComparableObjectArray> sourceRecord = getNextRecord(source);
				Pair<Object[], ComparableObjectArray> targetRecord = getNextRecord(target);

				while (sourceRecord != null || targetRecord != null) {
					if (sourceRecord != null && targetRecord != null) {
						final ComparableObjectArray sourceObjects = sourceRecord.getValue();
						final ComparableObjectArray targetObjects = targetRecord.getValue();
						final Object[] sourceKey = sourceRecord.getKey();
						final Object[] targetKey = targetRecord.getKey();
						int keyComparison = compareKeys(sourceKey, targetKey);
						if (keyComparison == 0) {
							// same Keys
							if (!sourceObjects.equalsCompareFields(targetObjects, comparison.getComparators(),
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

		private Pair<Object[], ComparableObjectArray> getNextRecord(QueueingQueryActions actions)
				throws InterruptedException {
			if (actions.isExhausted() && actions.queue.isEmpty())
				return null;
			Pair<Object[], ComparableObjectArray> objects = actions.queue.poll(100, TimeUnit.MILLISECONDS);
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
