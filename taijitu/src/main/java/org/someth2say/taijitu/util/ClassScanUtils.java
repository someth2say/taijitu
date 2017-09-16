package org.someth2say.taijitu.util;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;
import org.apache.log4j.Logger;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Jordi Sola on 22/02/2017.
 */
public class ClassScanUtils {
    private static final Logger logger = Logger.getLogger(ClassScanUtils.class);

    private ClassScanUtils() {
    }

    public static <T extends Named> Map<String, T> getInstancesForClassesImplementing(Class<T> implementedInterface) {
        Map<String, T> result = new ConcurrentHashMap<>();
        final FastClasspathScanner fcs = new FastClasspathScanner();
        final ScanResult scanResult = fcs.scan();
        final List<String> classNames = scanResult.getNamesOfClassesImplementing(implementedInterface);
        final List<Class<?>> clazzes = scanResult.classNamesToClassRefs(classNames);
        for (Class<?> clazz : clazzes) {
            try {
                if (!Modifier.isAbstract(clazz.getModifiers())) {
                    @SuppressWarnings("unchecked")
                    T plugin = (T) clazz.newInstance();
                    result.put(plugin.getName(), plugin);
                }
            } catch (InstantiationException | IllegalAccessException e) {
                logger.error("Unable to instantiate plugin class " + clazz.getName(), e);
            }
        }
        return result;
    }
}
