package org.someth2say.taijitu.registry;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.TaijituException;
import org.someth2say.taijitu.matcher.ColumnMatcher;
import org.someth2say.taijitu.matcher.NamingColumnMatcher;
import org.someth2say.taijitu.matcher.PositionalColumnMatcher;
import org.someth2say.taijitu.util.ClassScanUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Jordi Sola on 16/02/2017.
 */
public class MatcherRegistry {
    private static final Logger logger = Logger.getLogger(MatcherRegistry.class);
    private static Map<String, ColumnMatcher> instances = new ConcurrentHashMap<>();

    private MatcherRegistry() {
    }

    public static ColumnMatcher getMatcher(final String matcherName) throws TaijituException {
        if (instances.containsKey(matcherName)) {
            return instances.get(matcherName);
        }
        throw new TaijituException("Comparison strategy not found: " + matcherName);
    }

    public static void scanClassPath() {
        // This seems fast enough for a one-shot initialization
        // If found slow, it can be changed to scan only sub-packages for taijitu
        // final FastClasspathScanner fcs = new FastClasspathScanner(taijitu.class.getPackage().getName());
        final Class<ColumnMatcher> implementedInterface = ColumnMatcher.class;
        instances = ClassScanUtils.getInstancesForClassesImplementing(implementedInterface);
        logger.info("Registered matchers: " + instances.keySet().toString());
    }

    public static void useDefaults() {
        instances = new ConcurrentHashMap<>();
        addMatchers(new NamingColumnMatcher());
        addMatchers(new PositionalColumnMatcher());
    }

    private static void addMatchers(ColumnMatcher matcher) {
        instances.put(matcher.getName(), matcher);
    }
}
