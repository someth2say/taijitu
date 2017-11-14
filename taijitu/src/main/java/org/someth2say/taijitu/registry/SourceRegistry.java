package org.someth2say.taijitu.registry;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.someth2say.taijitu.config.interfaces.ISourceCfg;
import org.someth2say.taijitu.source.AbstractSource;
import org.someth2say.taijitu.source.Source;
import org.someth2say.taijitu.source.csv.CSVResourceSource;
import org.someth2say.taijitu.source.query.QuerySource;
import org.someth2say.taijitu.util.ClassScanUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Jordi Sola on 16/02/2017.
 */
public class SourceRegistry {
    private static final Logger logger = Logger.getLogger(SourceRegistry.class);
    private static Map<String, Class<? extends AbstractSource>> classes = new ConcurrentHashMap<>();

    private SourceRegistry() {
    }

    public static void scanClassPath() {
        // This seems fast enough for a one-shot initialization
        // If found slow, it can be changed to scan only sub-packages
        classes = ClassScanUtils.getClassesImplementing(AbstractSource.class);
        logger.info("Registered source types: " + StringUtils.join(classes, ","));
    }

    public static void useDefaults() {
        addSourceType(QuerySource.NAME, QuerySource.class);
        addSourceType(CSVResourceSource.NAME, CSVResourceSource.class);
    }

    private static <T extends AbstractSource> void addSourceType(String name, Class<T> clazz) {
        classes.put(name, clazz);
    }

    private static Class<? extends AbstractSource> getSourceType(String type) {
        return classes.get(type);
    }

    public static <T> Source<T> getInstance(String type, ISourceCfg sourceConfig) {
        Class<? extends AbstractSource> sourceClass = getSourceType(type);
        try {
            //TODO: Fix this unckecked assignment
            return sourceClass.getDeclaredConstructor(ISourceCfg.class).newInstance(sourceConfig);
        } catch (Exception e) {
            Object[] arguments = {sourceConfig};
            logger.error("Unable to create source. Type: " + type + " Arguments: " + StringUtils.join(arguments, ","), e);
        }
        return null;
    }

}

