package org.someth2say.taijitu.strategy;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.TaijituException;
import org.someth2say.taijitu.strategy.mapping.ParallelComparingMappingStrategy;
import org.someth2say.taijitu.strategy.mapping.ParallelQueryingMappingStrategy;
import org.someth2say.taijitu.strategy.sorted.SortedStrategy;
import org.someth2say.taijitu.util.ClassScanUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Jordi Sola on 16/02/2017.
 */
public class ComparisonStrategyRegistry {
    private static final Logger logger = Logger.getLogger(ComparisonStrategyRegistry.class);
    private static Map<String, ComparisonStrategy> instances = new ConcurrentHashMap<>();

    private ComparisonStrategyRegistry() {
    }

    public static ComparisonStrategy getStrategy(String strategyName) throws TaijituException {
        if (instances.containsKey(strategyName)) {
            return instances.get(strategyName);
        }
        throw new TaijituException("Comparison strategy not found: " + strategyName);
    }

    public static void scanClassPath() {
        // This seems fast enough for a one-shot initialization
        // If found slow, it can be changed to scan only sub-packages for taijitu
        // final FastClasspathScanner fcs = new FastClasspathScanner(taijitu.class.getPackage().getName());
        final Class<ComparisonStrategy> implementedInterface = ComparisonStrategy.class;
        instances = ClassScanUtils.getInstancesForClassesImplementing(implementedInterface);
        logger.info("Registered strategies: " + instances.keySet().toString());
    }

    public static void useDefaults() {
        instances = new ConcurrentHashMap<>();
        addStrategy(new ParallelQueryingMappingStrategy());
        addStrategy(new ParallelComparingMappingStrategy());
        addStrategy(new SortedStrategy());
    }

    private static void addStrategy(ComparisonStrategy strategy) {
        instances.put(strategy.getName(), strategy);
    }
}
