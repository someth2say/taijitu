package org.someth2say.taijitu.registry;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.compare.equality.CaseInsensitiveEqualityStrategy;
import org.someth2say.taijitu.compare.equality.EqualityStrategy;
import org.someth2say.taijitu.compare.equality.ToStringEqualityStrategy;
import org.someth2say.taijitu.compare.equality.ValueThresholdEqualityStrategy;
import org.someth2say.taijitu.util.ClassScanUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Jordi Sola on 16/02/2017.
 */
public class EqualityStrategyRegistry {
    private static final Logger logger = Logger.getLogger(EqualityStrategyRegistry.class);
    private static Map<String, EqualityStrategy> instances = new ConcurrentHashMap<>();

    private EqualityStrategyRegistry() {
    }

    public static EqualityStrategy getEqualityStrategy(final String name) {
        return instances.get(name);
    }

    public static void scanClassPath() {
        // This seems fast enough for a one-shot initialization
        // If found slow, it can be changed to scan only sub-packages
        final Class<EqualityStrategy> implementedInterface = EqualityStrategy.class;
        instances = ClassScanUtils.getInstancesForClassesImplementing(implementedInterface);
        logger.info("Registered matchers: " + instances.keySet().toString());
    }

    public static void useDefaults() {
        instances = new ConcurrentHashMap<>();
        addEqualityStrategy(new ToStringEqualityStrategy());
        addEqualityStrategy(new CaseInsensitiveEqualityStrategy());
        addEqualityStrategy(new ValueThresholdEqualityStrategy());

    }

    private static void addEqualityStrategy(EqualityStrategy equalityStrategy) {
        instances.put(equalityStrategy.getName(), equalityStrategy);
    }

}
