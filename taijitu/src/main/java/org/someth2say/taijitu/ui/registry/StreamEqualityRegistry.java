package org.someth2say.taijitu.ui.registry;

import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.compare.equality.ComparableCategorizerEquality;
import org.someth2say.taijitu.compare.equality.Equality;
import org.someth2say.taijitu.compare.equality.stream.AbstractStreamEquality;
import org.someth2say.taijitu.compare.equality.stream.StreamEquality;
import org.someth2say.taijitu.compare.equality.stream.mapping.MappingStreamEquality;
import org.someth2say.taijitu.compare.equality.stream.sorted.ComparableStreamEquality;
import org.someth2say.taijitu.ui.util.ClassScanUtils;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Jordi Sola on 16/02/2017.
 */
public class StreamEqualityRegistry {
    private static final Logger logger = LoggerFactory.getLogger(StreamEqualityRegistry.class);
    private static Map<String, Class<? extends AbstractStreamEquality>> classes = new ConcurrentHashMap<>();

    private StreamEqualityRegistry() {
    }

    private static Class<? extends AbstractStreamEquality> getStrategyType(final String type) {
        return classes.get(type);
    }

    public static void scanClassPath() {
        // This seems fast enough for a one-shot initialization
        // If found slow, it can be changed to scan only sub-packages for taijitu
        // final FastClasspathScanner fcs = new FastClasspathScanner(taijitu.class.getPackage().getName());
        Collection<Class<? extends AbstractStreamEquality>> equalityClasses = ClassScanUtils.getClassesImplementing(AbstractStreamEquality.class);
        equalityClasses.forEach(StreamEqualityRegistry::addStreamEqualityType);

        logger.info("Registered stream equalities: {}", StreamEqualityRegistry.classes.keySet().toString());
    }

    public static void useDefaults() {
        addStreamEqualityType(MappingStreamEquality.class);
        addStreamEqualityType(ComparableStreamEquality.class);
    }

    private static <T extends AbstractStreamEquality<?>> void addStreamEqualityType(Class<T> clazz) {
        classes.put(clazz.getSimpleName(), clazz);
    }

    public static <T> StreamEquality<T> getInstance(String type, Equality<T> equality, ComparableCategorizerEquality<T> categorizer) {
        //TODO: Fix this unchecked cast
        Class<? extends AbstractStreamEquality<T>> strategyType = (Class<? extends AbstractStreamEquality<T>>) getStrategyType(type);
        try {
            return strategyType.getDeclaredConstructor(Equality.class, ComparableCategorizerEquality.class).newInstance(equality, categorizer);
        } catch (Exception e) {
            logger.error("Unable to create stream equality. Type: " + type + " Arguments: " + equality, e);
        }
        return null;
    }

}
