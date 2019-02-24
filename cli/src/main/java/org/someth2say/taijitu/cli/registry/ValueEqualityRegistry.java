package org.someth2say.taijitu.cli.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.cli.util.ClassScanUtils;
import org.someth2say.taijitu.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.equality.impl.value.*;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Jordi Sola on 16/02/2017.
 */
public class ValueEqualityRegistry {
    private static final Logger logger = LoggerFactory.getLogger(ValueEqualityRegistry.class);
    private static Map<String, Class<? extends Equalizer>> classes = new ConcurrentHashMap<>();

    private ValueEqualityRegistry() {
    }

    private static Class<? extends Equalizer> getValueEqualityType(final String name) {
        return classes.get(name);
    }

    public static void scanClassPath() {
        // This seems fast enough for a one-shot initialization
        // If found slow, it can be changed to scan only sub-packages
        final Class<Equalizer> implementedInterface = Equalizer.class;

        Collection<Class<? extends Equalizer>> equalityClasses = ClassScanUtils.getClassesImplementing(implementedInterface);
        equalityClasses.forEach(ValueEqualityRegistry::addEqualityStrategy);
        logger.info("Registered value equalities: {}", classes.values().stream().map(ClassScanUtils::getClassName));
    }

    public static void useDefaults() {
        addEqualityStrategy(ObjectToStringComparatorHasher.class);
        addEqualityStrategy(StringCaseInsensitiveComparatorHasher.class);
        addEqualityStrategy(NumberThresholdComparatorHasher.class);
        addEqualityStrategy(DateThresholdComparator.class);
        addEqualityStrategy(ComparableComparatorHasher.class);
        addEqualityStrategy(ObjectHasher.class);
    }

    private static void addEqualityStrategy(Class<? extends Equalizer> clazz) {
        classes.put(ClassScanUtils.getClassName(clazz), clazz);
    }

    public static Equalizer getInstance(String type, Object equalityConfig) {
        Class<? extends Equalizer> equalityType = getValueEqualityType(type);
        try {
            //TODO: Fix this unckecked assignment
            return equalityType.getDeclaredConstructor(Object.class).newInstance(equalityConfig);
        } catch (Exception e) {
            logger.error("Unable to create stream equality. Type: " + type + " Arguments: " + equalityConfig, e);
        }
        return null;
    }
}
