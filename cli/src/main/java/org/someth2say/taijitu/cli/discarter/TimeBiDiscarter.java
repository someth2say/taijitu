package org.someth2say.taijitu.discarter;

import java.util.function.BiConsumer;

/**
 * @author Jordi Sola
 */
public class TimeBiDiscarter<T, Q> implements BiConsumer<T, Q> {
    private final long delayTime;
    private final BiConsumer<T, Q> consumer;
    private long lastTimeExecuted;

    public TimeBiDiscarter(long _delayTime, BiConsumer<T, Q> function) {
        delayTime = _delayTime;
        lastTimeExecuted = System.currentTimeMillis();
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
