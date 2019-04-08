package org.someth2say.taijitu.cli.util;

import io.github.fastclasspathscanner.ClassInfoList;
import io.github.fastclasspathscanner.FastClasspathScanner;
import io.github.fastclasspathscanner.ScanResult;

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
public abstract class ClassScanUtils {
    
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
        return classes.collect(Collectors.toConcurrentMap(clazz -> clazz.getSimpleName(), Function.identity()));
    }

    @SuppressWarnings("unchecked")
	private static <T> Stream<Class<? extends T>> getClassesFor(Class<T> clazzOrInterface) {
        final FastClasspathScanner fcs = new FastClasspathScanner().enableClassInfo();
        final ScanResult scanResult = fcs.scan();
        final ClassInfoList classInfoList = clazzOrInterface.isInterface() ? scanResult.getClassesImplementing(clazzOrInterface.getName()) : scanResult.getSubclasses(clazzOrInterface.getName());
        List<Class<?>> classes = classInfoList.loadClasses();
        return classes.stream().map(t -> (Class<? extends T>) t);
    }

    private static <T> boolean isNotAbstract(Class<? extends T> clazz) {
        return !Modifier.isAbstract(clazz.getModifiers());
    }

    private static <T> boolean isNotAnonimous(Class<? extends T> clazz) {
        return !clazz.isAnonymousClass();
    }
}
