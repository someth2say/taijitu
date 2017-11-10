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
public class ValueEqualityRegistry {
    private static final Logger logger = Logger.getLogger(ValueEqualityRegistry.class);
    private static Map<String, Class<? extends AbstractValueEquality>> classes = new ConcurrentHashMap<>();

    private ValueEqualityRegistry() {
    }

    private static Class<? extends AbstractValueEquality> getValueEqualityType(final String name) {
        return classes.get(name);
    }

    public static void scanClassPath() {
        // This seems fast enough for a one-shot initialization
        // If found slow, it can be changed to scan only sub-packages
        final Class<AbstractValueEquality> implementedInterface = AbstractValueEquality.class;
        classes = ClassScanUtils.getClassesImplementing(implementedInterface);

        logger.info("Registered value equalities: " + classes.keySet().toString());
    }

    public static void useDefaults() {
        addEqualityStrategy(ToStringValueEquality.NAME, ToStringValueEquality.class);
        addEqualityStrategy(CaseInsensitiveValueEquality.NAME, CaseInsensitiveValueEquality.class);
        addEqualityStrategy(NumberThresholdValueEquality.NAME, NumberThresholdValueEquality.class);
        addEqualityStrategy(TimestampThresholdValueEquality.NAME, TimestampThresholdValueEquality.class);
        addEqualityStrategy(JavaComparableValueEquality.NAME, JavaComparableValueEquality.class);
        addEqualityStrategy(JavaObjectValueEquality.NAME, JavaObjectValueEquality.class);
    }

    private static void addEqualityStrategy(String name, Class<? extends AbstractValueEquality> clazz) {
        classes.put(name, clazz);
    }

    public static <T> AbstractValueEquality<T> getInstance(String type, Object equalityConfig) {
        //TODO: Fix this unckecked cast
        Class<? extends AbstractValueEquality> equalityType = getValueEqualityType(type);
        try {
            return equalityType.getDeclaredConstructor(Object.class).newInstance(equalityConfig);
        } catch (Exception e) {
            logger.error("Unable to create stream equality. Type: " + type + " Arguments: " + equalityConfig, e);
        }
        return null;
    }
}
