package org.someth2say.taijitu.strategy;

/**
 * Created by Jordi Sola on 09/03/2017.
 */
public interface ExceptionHoldingRunnable<T extends Exception> extends Runnable {
    T getException();
}
