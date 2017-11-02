package org.someth2say.taijitu.registry;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.someth2say.taijitu.source.csv.CSVFileSource;
import org.someth2say.taijitu.source.query.ResultSetSource;
import org.someth2say.taijitu.source.Source;
import org.someth2say.taijitu.util.ClassScanUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Jordi Sola on 16/02/2017.
 */
public class SourceTypeRegistry {
    private static final Logger logger = Logger.getLogger(SourceTypeRegistry.class);
    private static Map<String, Class<? extends Source>> classes = new ConcurrentHashMap<>();

    private SourceTypeRegistry() {
    }

    public static void scanClassPath() {
        // This seems fast enough for a one-shot initialization
        // If found slow, it can be changed to scan only sub-packages
        classes = ClassScanUtils.getClassesImplementing(Source.class);
        logger.info("Registered source types: " + StringUtils.join(classes, ","));
    }

    public static void useDefaults() {
        addSourceType(ResultSetSource.NAME, ResultSetSource.class);
        addSourceType(CSVFileSource.NAME, CSVFileSource.class);
    }

    private static <T extends Source> void addSourceType(String name, Class<T> clazz) {
        classes.put(name, clazz);
    }

    public static Class<? extends Source> getSourceType(String type) {
        return classes.get(type);
    }
}
