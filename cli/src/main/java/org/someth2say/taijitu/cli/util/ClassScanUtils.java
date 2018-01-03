package org.someth2say.taijitu.cli.util;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Jordi Sola on 22/02/2017.
 */
public class ClassScanUtils {
    private static final Logger logger = LoggerFactory.getLogger(ClassScanUtils.class);

    private ClassScanUtils() {
    }


    public static <T> Collection<Class<? extends T>> getClassesImplementing(Class<T> clazzOrInterface) {
        Stream<Class<? extends T>> classes = getClassesFor(clazzOrInterface)
                .filter(ClassScanUtils::isNotAbstract)
                .filter(ClassScanUtils::isNotAnonimous);
        return classes.collect(Collectors.toList());
    }

    public static <T> Map<String, Class<? extends T>> getNamedClassesImplementing(Class<T> clazzOrInterface) {
        Stream<Class<? extends T>> classes = getClassesFor(clazzOrInterface)
                .filter(ClassScanUtils::isNotAbstract)
                .filter(ClassScanUtils::isNotAnonimous);
        return classes.collect(Collectors.toConcurrentMap(ClassScanUtils::getClassName, Function.identity()));
    }

    private static <T> Stream<Class<? extends T>> getClassesFor(Class<T> clazzOrInterface) {
        final FastClasspathScanner fcs = new FastClasspathScanner();
        final ScanResult scanResult = fcs.scan();
        final List<String> classNames = clazzOrInterface.isInterface() ? scanResult.getNamesOfClassesImplementing(clazzOrInterface) : scanResult.getNamesOfSubclassesOf(clazzOrInterface);
        List<Class<?>> classes = scanResult.classNamesToClassRefs(classNames);
        return classes.stream().map(t -> (Class<? extends T>) t);
    }

    private static boolean isStatic(Method namingMethod) {
        return (namingMethod.getModifiers() & Modifier.STATIC) != 0;
    }

    public static <T> String getClassName(Class<? extends T> clazz) {
        String name = clazz.getSimpleName();
        try {
            //TODO: Find a type-safer way...
            Method namingMethod = clazz.getDeclaredMethod("getName");
            if ((namingMethod != null) && isStatic(namingMethod)) {
                Object getNameResult = namingMethod.invoke(null);
                name = getNameResult.toString();
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            //logger.error("Unable to invoke `getName` from class {}", clazz.getName());
        }
        return name;
    }

    private static <T> boolean isNotAbstract(Class<? extends T> clazz) {
        return !Modifier.isAbstract(clazz.getModifiers());
    }

    private static <T> boolean isNotAnonimous(Class<? extends T> clazz) {
        return !clazz.isAnonymousClass();
    }
}
