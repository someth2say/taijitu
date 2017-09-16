package org.someth2say.taijitu.query.discarter;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 * Created by Jordi Sola on 16/02/2017.
 */
public class TimeBasedLog4jDiscarter extends TimeBasedDiscarter {

    private final Priority priority;
    private final boolean enabled;
    private StringBuilder sb;

    public TimeBasedLog4jDiscarter(long _delayTime, Logger logger, Priority priority) throws NoSuchMethodException {
        super(_delayTime, logger, logger.getClass().getMethod("log", Priority.class, Object.class));
        this.priority = priority;
        enabled = logger.isEnabledFor(priority);
        if (enabled) sb = new StringBuilder();
    }

    public static Discarter newInstance(long _delayTime, Logger logger, Priority priority) {
        try {
            return new TimeBasedLog4jDiscarter(_delayTime, logger, priority);
        } catch (NoSuchMethodException e) {
            return new NoOppDiscarter();
        }
    }

    @Override
    public Object execute(Object... parameters) {
        if (enabled) {
            sb.setLength(0);
            for (Object obj : parameters) {
                sb.append(obj.toString());
            }
            return super.execute(priority, sb.toString());
        }
        return null;
    }
}