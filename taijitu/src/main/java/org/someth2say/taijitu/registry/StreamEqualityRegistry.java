package org.someth2say.taijitu.registry;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.compare.equality.stream.AbstractStreamEquality;
import org.someth2say.taijitu.compare.equality.stream.StreamEquality;
import org.someth2say.taijitu.compare.equality.stream.mapping.MappingStreamEquality;
import org.someth2say.taijitu.compare.equality.stream.sorted.SortedStreamEquality;
import org.someth2say.taijitu.compare.equality.tuple.StructureEquality;
import org.someth2say.taijitu.util.ClassScanUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Jordi Sola on 16/02/2017.
 */
public class StreamEqualityRegistry {
    private static final Logger logger = Logger.getLogger(StreamEqualityRegistry.class);
    private static Map<String, Class<? extends AbstractStreamEquality<?>>> classes = new ConcurrentHashMap<>();

    private StreamEqualityRegistry() {
    }

    private static Class<? extends AbstractStreamEquality<?>> getStrategyType(final String type) {
        return classes.get(type);
    }

    public static void scanClassPath() {
        // This seems fast enough for a one-shot initialization
        // If found slow, it can be changed to scan only sub-packages for taijitu
        // final FastClasspathScanner fcs = new FastClasspathScanner(taijitu.class.getPackage().getName());
        final Class<AbstractStreamEquality> implementedInterface = AbstractStreamEquality.class;

        classes = ClassScanUtils.getClassesImplementing(implementedInterface);

        logger.info("Registered strategies: " + classes.keySet().toString());
    }

    public static void useDefaults() {
        addStreamEqualityType(MappingStreamEquality.NAME, MappingStreamEquality.class);
        addStreamEqualityType(SortedStreamEquality.NAME, SortedStreamEquality.class);
    }

    private static <T extends AbstractStreamEquality<?>> void addStreamEqualityType(String name, Class<T> clazz) {
        classes.put(name, clazz);
    }

    public static <T> StreamEquality<T> getInstance(String type, StructureEquality<T> equality, StructureEquality<T> categorizer) {
        //TODO: Fix this unchecked cast
        Class<? extends AbstractStreamEquality<T>> strategyType = (Class<? extends AbstractStreamEquality<T>>) getStrategyType(type);
        try {
            return strategyType.getDeclaredConstructor(StructureEquality.class, StructureEquality.class).newInstance(equality, categorizer);
        } catch (Exception e) {
            logger.error("Unable to create stream equality. Type: " + type + " Arguments: " + equality, e);
        }
        return null;
    }

}
