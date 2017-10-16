package org.someth2say.taijitu.discarter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Jordi Sola
 */
public class TimeBasedDiscarter implements Discarter {
    private final long delayTime;
    private final Object object;
    private final Method method;
    private long lastTimeExecuted = 0L;

    public TimeBasedDiscarter(long _delayTime, Object _object, Method _method) {
        this.lastTimeExecuted = System.currentTimeMillis();
        this.delayTime = _delayTime;
        this.object = _object;
        this.method = _method;
    }

    @Override
    public Object execute(Object... parameters) {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - this.lastTimeExecuted;
        if (elapsedTime > this.delayTime) {
            this.lastTimeExecuted = currentTime;
            try {
                return this.method.invoke(this.object, parameters);
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                // Silently ignore
            }
        }
        return null;
    }
}
