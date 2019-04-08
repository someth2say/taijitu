package org.someth2say.taijitu.cli.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.cli.util.ClassScanUtils;
import org.someth2say.taijitu.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.equality.impl.value.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Jordi Sola on 16/02/2017.
 */
public class EqualizerRegistry {
    private static final Logger logger = LoggerFactory.getLogger(EqualizerRegistry.class);
    private static Map<String, Class<? extends Equalizer>> classes = new ConcurrentHashMap<>();

    private EqualizerRegistry() {
    }

    private static Class<? extends Equalizer> getValueEqualityType(final String name) {
        return classes.get(name);
    }

    public static void scanClassPath() {
        // This seems fast enough for a one-shot initialization
        // If found slow, it can be changed to scan only sub-packages
        classes = ClassScanUtils.getNamedClassesImplementing(Equalizer.class);
        logger.info("Registered equalizers: {}", classes.keySet().toString());
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
        classes.put(clazz.getSimpleName(), clazz);
    }

    public static Equalizer getInstance(String type) {
        Class<? extends Equalizer> equalityType = getValueEqualityType(type);

        try {
            return equalityType.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            logger.error("Unable get equalizer instance. Type: " + type, e);
        }
        return null;
    }
}
