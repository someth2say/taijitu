package org.someth2say.taijitu.strategy;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.query.Query;
import org.someth2say.taijitu.query.QueryUtilsException;
import org.someth2say.taijitu.query.queryactions.QueryActions;
import org.someth2say.taijitu.query.querywalker.QueryWalker;
import org.someth2say.taijitu.TaijituException;
import org.someth2say.taijitu.ComparisonRuntime;
import org.someth2say.taijitu.compare.ComparableObjectArray;
import org.someth2say.taijitu.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Jordi Sola on 07/03/2017.
 */
public class StrategyUtils {
    private static final Logger logger = Logger.getLogger(StrategyUtils.class);

    private StrategyUtils() {
    }

	public static void runParallelQueryActions(ComparisonRuntime comparison, Collection<Pair<Query, QueryActions<ComparableObjectArray>>> actions, ExceptionHoldingRunnable<? extends Exception>... otherRunnables) throws TaijituException {
        Collection<Collection<Exception>> allExceptions = new ArrayList<>(actions.size());
        
        final ExecutorService executor = Executors.newFixedThreadPool(actions.size() + otherRunnables.length);
        
        for (Pair<Query, QueryActions<ComparableObjectArray>> pair : actions) {
            final Collection<Exception> pairExceptions = StrategyUtils.executeQueryActions(pair.getKey(), executor, pair.getValue());
            allExceptions.add(pairExceptions);
        }
        
        for (ExceptionHoldingRunnable<? extends Exception> runnable : otherRunnables) {
            executor.execute(runnable);
        }

        StrategyUtils.waitForFinalization(comparison.getTestName(), executor);

        Collection<Exception> flat = allExceptions.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        reThrowAll(flat, "Exception performing query actions");
    }

    public static void reThrowAll(Collection<Exception> exceptions, String message) throws TaijituException {
        TaijituException result = null;
        for (Exception exeption : exceptions) {
            if (exeption != null) {
                if (result == null) {
                    result = new TaijituException(message, exeption);
                } else {
                    result.addSuppressed(exeption);
                }
            }
        }
        if (result != null) throw result;
    }


    static void waitForFinalization(String testName, ExecutorService threadPool) throws TaijituException {
        threadPool.shutdown();
        try {
            while (!threadPool.awaitTermination(1, TimeUnit.SECONDS)) {
                logger.debug("Awaiting for " + testName + " queries to complete.");
            }
        } catch (final InterruptedException t) {
            throw new TaijituException("Comparison terminated unexpectedly: " + t.getMessage(), t);
        }
    }

    static ExceptionHoldingRunnable<TaijituException> getComparisonFieldsUpdatingRunnable(final ComparisonRuntime comparison) {
        return new ExceptionHoldingRunnable<TaijituException>() {
            private TaijituException exception;

            @Override
            public TaijituException getException() {
                return exception;
            }

            @Override
            public void run() {
                // Once all threads passed this descriptionBarrier, fields have been calculated
                try {
                    comparison.calculateActualFields();
                } catch (TaijituException e) {
                    this.exception = e;
                }

            }
        };
    }

    static <T extends QueryActions<ComparableObjectArray>> Collection<Exception> executeQueryActions(final Query query, final ExecutorService executor, final T queryActions) {
        final Collection<Exception> exceptions = new ArrayList<>();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.debug("Start processing query " + query.getQueryName());
                    QueryWalker.walkValues(query, ComparableObjectArray.Factory.INSTANCE, queryActions);
                    logger.info("Completed processing query " + query.getQueryName());
                } catch (QueryUtilsException e) {
                    exceptions.add(e);
                    executor.shutdownNow();
                }
            }
        });
        return exceptions;
    }

}
