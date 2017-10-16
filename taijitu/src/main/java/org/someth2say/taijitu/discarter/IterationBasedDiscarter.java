package org.someth2say.taijitu.discarter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Jordi Sola
 */
public class IterationBasedDiscarter implements Discarter {
    private final long iterations;
    private final Object object;
    private final Method method;
    private long iterationsRemaining = 0L;

    public IterationBasedDiscarter(long _iterations, Object _object, Method _method) {
        this.iterationsRemaining = _iterations;
        this.iterations = _iterations;
        this.object = _object;
        this.method = _method;
    }

    public Object execute(Object... parameters) {
        --this.iterationsRemaining;
        if (this.iterationsRemaining <= 0L) {
            this.iterationsRemaining = this.iterations;

            try {
                return this.method.invoke(this.object, parameters);
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException var3) {
                // Silently ignore
            }
        }
        return null;
    }
}
