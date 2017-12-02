package org.someth2say.taijitu.cli.util;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Jordi Sola on 22/02/2017.
 */
public class ClassScanUtils {
    private static final Logger logger = LoggerFactory.getLogger(ClassScanUtils.class);

    private ClassScanUtils() {
    }

    public static <T> Collection<T> getInstancesForClassesImplementing(Class<? extends T> implementedInterface) {
        Collection<T> result = new ArrayList<>();
        final FastClasspathScanner fcs = new FastClasspathScanner();
        final ScanResult scanResult = fcs.scan();
        final List<String> classNames = scanResult.getNamesOfClassesImplementing(implementedInterface);
        final List<Class<?>> clazzes = scanResult.classNamesToClassRefs(classNames);
        for (Class<?> clazz : clazzes) {
            try {
                if (!Modifier.isAbstract(clazz.getModifiers())) {
                    @SuppressWarnings("unchecked")
                    T clazzInstance = (T) clazz.getDeclaredConstructor().newInstance();
                    result.add(clazzInstance);
                }
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                logger.error("Unable to instantiate plugin class " + clazz.getName(), e);
            }
        }
        return result;
    }

    public static <T extends Named> Map<String, T> getInstancesForNamedClassesImplementing(Class<? extends T> implementedInterface) {
        Map<String, T> result = new ConcurrentHashMap<>();
        final FastClasspathScanner fcs = new FastClasspathScanner();
        final ScanResult scanResult = fcs.scan();
        final List<String> classNames = scanResult.getNamesOfClassesImplementing(implementedInterface);
        final List<Class<?>> clazzes = scanResult.classNamesToClassRefs(classNames);
        for (Class<?> clazz : clazzes) {
            try {
                if (!Modifier.isAbstract(clazz.getModifiers())) {
                    @SuppressWarnings("unchecked")
                    T clazzInstance = (T) clazz.getDeclaredConstructor().newInstance();
                    result.put(clazzInstance.getName(), clazzInstance);
                }
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                logger.error("Unable to instantiate plugin class " + clazz.getName(), e);
            }
        }
        return result;
    }

    public static <T> Collection<Class<? extends T>> getClassesImplementing(Class<T> clazzOrInterface) {
        Collection<Class<? extends T>> result = new ArrayList<>();
        final FastClasspathScanner fcs = new FastClasspathScanner();
        final ScanResult scanResult = fcs.scan();

        final List<String> classNames = clazzOrInterface.isInterface() ? scanResult.getNamesOfClassesImplementing(clazzOrInterface) : scanResult.getNamesOfSubclassesOf(clazzOrInterface);
        final List<Class<?>> classes = scanResult.classNamesToClassRefs(classNames);
        for (Class<?> clazz : classes) {
            if (!Modifier.isAbstract(clazz.getModifiers())) {
                @SuppressWarnings("unchecked")
                Class<? extends T> tClazz = (Class<? extends T>) clazz;
                result.add(tClazz);
            }
        }
        return result;
    }


    public static <T extends Named> Map<String, Class<? extends T>> getNamedClassesImplementing(Class<T> clazzOrInterface) {
        Map<String, Class<? extends T>> result = new ConcurrentHashMap<>();
        final FastClasspathScanner fcs = new FastClasspathScanner();
        final ScanResult scanResult = fcs.scan();

        final List<String> classNames = clazzOrInterface.isInterface() ? scanResult.getNamesOfClassesImplementing(clazzOrInterface) : scanResult.getNamesOfSubclassesOf(clazzOrInterface);
        final List<Class<?>> clazzes = scanResult.classNamesToClassRefs(classNames);
        for (Class<?> clazz : clazzes) {
            if (!Modifier.isAbstract(clazz.getModifiers())) {
                try {
                    //TODO: Find a type-safer way...
                    Method namingMethod = clazz.getDeclaredMethod("getName");
                    if (namingMethod != null) {
                        Object getNameResult = namingMethod.invoke(null);
                        String name = getNameResult.toString();

                        @SuppressWarnings("unchecked")
                        Class<? extends T> named = (Class<? extends T>) clazz;
                        result.put(name, named);
                    }
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    logger.error("Unable to invoke `getName` from class {}", clazz.getName());
                }
            }
        }
        return result;
    }


}
