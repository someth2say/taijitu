package org.someth2say.taijitu.discarter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Jordi Sola
 */
public class TimeBiDiscarter<T, Q> implements BiConsumer<T,Q> {
    private final long delayTime;
    private final BiConsumer<T, Q> consumer;
    private long lastTimeExecuted = 0L;

    public TimeBiDiscarter(long _delayTime, BiConsumer<T, Q> function) {
        delayTime = _delayTime;
        this.consumer = function;
    }

    @Override
    public void accept(T t, Q q) {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - this.lastTimeExecuted;
        if (elapsedTime > this.delayTime) {
            this.lastTimeExecuted = currentTime;
            consumer.accept(t, q);
        }
    }

}
