package org.someth2say.taijitu.strategy;

import org.someth2say.taijitu.TaijituException;
import org.someth2say.taijitu.ComparisonRuntime;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Jordi Sola on 09/03/2017.
 */
public class ExceptionHoldingCyclicBarrier {
    private final ExceptionHoldingRunnable<TaijituException> runnable;
    private final CyclicBarrier cyclicBarrier;

    public ExceptionHoldingCyclicBarrier(int parties, ComparisonRuntime comparison) {
        runnable = StrategyUtils.getComparisonFieldsUpdatingRunnable(comparison);
        cyclicBarrier = new CyclicBarrier(parties, runnable);
    }

    public CyclicBarrier getCyclicBarrier() {
        return cyclicBarrier;
    }

    public TaijituException getException() {
        return runnable.getException();
    }

    public ExceptionHoldingRunnable<TaijituException> getRunnable() {
        return runnable;
    }

    public int getParties() {
        return cyclicBarrier.getParties();
    }

    public int await() throws InterruptedException, BrokenBarrierException {
        return cyclicBarrier.await();
    }

    public int await(long l, TimeUnit timeUnit) throws InterruptedException, BrokenBarrierException, TimeoutException {
        return cyclicBarrier.await(l, timeUnit);
    }

    public boolean isBroken() {
        return cyclicBarrier.isBroken();
    }

    public void reset() {
        cyclicBarrier.reset();
    }

    public int getNumberWaiting() {
        return cyclicBarrier.getNumberWaiting();
    }
}
