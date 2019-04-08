package org.someth2say.taijitu.cli.registry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.cli.config.interfaces.ISourceCfg;
import org.someth2say.taijitu.cli.source.AbstractSource;
import org.someth2say.taijitu.cli.source.Source;
import org.someth2say.taijitu.cli.source.csv.CSVResourceSource;
import org.someth2say.taijitu.cli.source.query.QuerySource;
import org.someth2say.taijitu.cli.util.ClassScanUtils;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Jordi Sola on 16/02/2017.
 */
public class SourceRegistry {
    private static final Logger logger = LoggerFactory.getLogger(SourceRegistry.class);
    private static Map<String, Class<? extends AbstractSource>> classes = new ConcurrentHashMap<>();

    private SourceRegistry() {
    }

    public static void scanClassPath() {
        // This seems fast enough for a one-shot initialization
        // If found slow, it can be changed to scan only sub-packages
        classes = ClassScanUtils.getNamedClassesImplementing(AbstractSource.class);
        logger.info("Registered sources: {}", StringUtils.join(classes.keySet(), ","));
    }

    public static void useDefaults() {
        addSourceType(QuerySource.class);
        addSourceType(CSVResourceSource.class);
    }

    private static <T extends AbstractSource> void addSourceType(Class<T> clazz) {
        classes.put(clazz.getSimpleName(), clazz);
    }

    private static Class<? extends AbstractSource> getSourceType(String type) {
        return classes.get(type);
    }

    public static <T> Source<T> getInstance(String type, ISourceCfg sourceConfig) {
        Class<? extends AbstractSource> sourceClass = getSourceType(type);
        Properties buildProperties = sourceConfig.getBuildProperties();
        Properties fetchProperties = sourceConfig.getFetchProperties();
        try {
            Constructor<? extends AbstractSource> constructor = sourceClass.getDeclaredConstructor(String.class, Properties.class, Properties.class);
            @SuppressWarnings("unchecked")
            AbstractSource<T> abstractSource = constructor.newInstance(sourceConfig.getName(), buildProperties, fetchProperties);
            return abstractSource;
        } catch (Exception e) {
            logger.error("Unable to create source. Type: {}  Arguments: {}",type, StringUtils.join(new Object[]{buildProperties, fetchProperties}, ","), e);
        }
        return null;
    }

}

