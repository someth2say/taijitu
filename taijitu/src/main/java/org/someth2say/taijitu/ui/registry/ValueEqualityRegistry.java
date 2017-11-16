package org.someth2say.taijitu.ui.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.compare.equality.value.*;
import org.someth2say.taijitu.ui.util.ClassScanUtils;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Jordi Sola on 16/02/2017.
 */
@SuppressWarnings("rawtypes")
public class ValueEqualityRegistry {
    private static final Logger logger = LoggerFactory.getLogger(ValueEqualityRegistry.class);
    private static Map<String, Class<? extends AbstractConfigurableEquality>> classes = new ConcurrentHashMap<>();

    private ValueEqualityRegistry() {
    }

    private static Class<? extends AbstractConfigurableEquality> getValueEqualityType(final String name) {
        return classes.get(name);
    }

    public static void scanClassPath() {
        // This seems fast enough for a one-shot initialization
        // If found slow, it can be changed to scan only sub-packages
        final Class<AbstractConfigurableEquality> implementedInterface = AbstractConfigurableEquality.class;

        Collection<Class<? extends AbstractConfigurableEquality>> equalityClasses = ClassScanUtils.getClassesImplementing(implementedInterface);
        equalityClasses.forEach(ValueEqualityRegistry::addEqualityStrategy);
        logger.info("Registered value equalities: {}", classes.keySet().toString());
    }

    public static void useDefaults() {
        addEqualityStrategy(ObjectToString.class);
        addEqualityStrategy(StringCaseInsensitive.class);
        addEqualityStrategy(NumberThreshold.class);
        addEqualityStrategy(DateThreshold.class);
        addEqualityStrategy(JavaComparable.class);
        addEqualityStrategy(JavaObject.class);
    }

    private static void addEqualityStrategy(Class<? extends AbstractConfigurableEquality> clazz) {
        classes.put(clazz.getSimpleName(), clazz);
    }

    public static <T> AbstractConfigurableEquality<T> getInstance(String type, Object equalityConfig) {
        Class<? extends AbstractConfigurableEquality> equalityType = getValueEqualityType(type);
        try {
            //TODO: Fix this unckecked assignment
            return equalityType.getDeclaredConstructor(Object.class).newInstance(equalityConfig);
        } catch (Exception e) {
            logger.error("Unable to create stream equality. Type: " + type + " Arguments: " + equalityConfig, e);
        }
        return null;
    }
}
