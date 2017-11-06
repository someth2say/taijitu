package org.someth2say.taijitu.registry;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.compare.equality.value.*;
import org.someth2say.taijitu.util.ClassScanUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Jordi Sola on 16/02/2017.
 */
@SuppressWarnings("rawtypes")
public class EqualityStrategyRegistry {
    private static final Logger logger = Logger.getLogger(EqualityStrategyRegistry.class);
	private static Map<String, ValueEquality> instances = new ConcurrentHashMap<>();

    private EqualityStrategyRegistry() {
    } 

    public static ValueEquality<?> getEqualityStrategy(final String name) {
        return instances.get(name);
    }

    public static void scanClassPath() {
        // This seems fast enough for a one-shot initialization
        // If found slow, it can be changed to scan only sub-packages
        final Class<ValueEquality> implementedInterface = ValueEquality.class;
        instances = ClassScanUtils.getInstancesForClassesImplementing(implementedInterface);
        logger.info("Registered matchers: " + instances.keySet().toString());
    }

    public static void useDefaults() {
        instances = new ConcurrentHashMap<>();
        addEqualityStrategy(new ToStringValueEquality());
        addEqualityStrategy(new CaseInsensitiveValueEquality());
        addEqualityStrategy(new NumberThresholdValueEquality());
        addEqualityStrategy(new TimestampThresholdValueEquality());
    }

    private static void addEqualityStrategy(ValueEquality valueEquality) {
        instances.put(valueEquality.getName(), valueEquality);
    }

}
