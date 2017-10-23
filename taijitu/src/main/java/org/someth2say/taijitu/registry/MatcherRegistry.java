package org.someth2say.taijitu.registry;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.matcher.FieldMatcher;
import org.someth2say.taijitu.matcher.IdentityFieldMatcher;
import org.someth2say.taijitu.matcher.NamingFieldMatcher;
import org.someth2say.taijitu.matcher.PositionalFieldMatcher;
import org.someth2say.taijitu.util.ClassScanUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Jordi Sola on 16/02/2017.
 */
public class MatcherRegistry {
    private static final Logger logger = Logger.getLogger(MatcherRegistry.class);
    private static Map<String, FieldMatcher> instances = new ConcurrentHashMap<>();

    private MatcherRegistry() {
    }

    public static FieldMatcher getMatcher(final String matcherName) {
        return instances.get(matcherName);
    }

    public static void scanClassPath() {
        // This seems fast enough for a one-shot initialization
        // If found slow, it can be changed to scan only sub-packages
        final Class<FieldMatcher> implementedInterface = FieldMatcher.class;
        instances = ClassScanUtils.getInstancesForClassesImplementing(implementedInterface);
        logger.info("Registered matchers: " + instances.keySet().toString());
    }

    public static void useDefaults() {
        instances = new ConcurrentHashMap<>();
        addMatchers(new IdentityFieldMatcher());
        addMatchers(new NamingFieldMatcher());
        addMatchers(new PositionalFieldMatcher());
    }

    private static void addMatchers(FieldMatcher matcher) {
        instances.put(matcher.getName(), matcher);
    }

    public static FieldMatcher getIdentityMatcher() {
        return instances.computeIfAbsent(IdentityFieldMatcher.NAME, s -> new IdentityFieldMatcher());
    }
}
