package org.someth2say.taijitu.cli.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.cli.util.ClassScanUtils;
import org.someth2say.taijitu.cli.plugins.TaijituPlugin;
import org.someth2say.taijitu.cli.plugins.logging.TimeLoggingPlugin;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Jordi Sola on 22/02/2017.
 */
public class PluginRegistry {

    private static final Logger logger = LoggerFactory.getLogger(PluginRegistry.class);
    private static Map<String, TaijituPlugin> instances;

    private PluginRegistry() {
    }

    //TODO: This does not respect the order of plugins
    public static Collection<TaijituPlugin> getPlugins() {
        return instances.values();
    }

    public static void scanClassPath() {
        // This seems fast enough for a one-shot initialization
        // If found slow, it can be changed to scan only sub-packages for taijitu
        final Class<TaijituPlugin> implementedInterface = TaijituPlugin.class;
        instances = ClassScanUtils.getInstancesForNamedClassesImplementing(implementedInterface);
        logger.info("Registered plugins: {}", instances.keySet().toString());
    }

    public static void useDefaults() {
        instances = new ConcurrentHashMap<>();
        addPlugin(new TimeLoggingPlugin());
    }

    private static void addPlugin(TaijituPlugin plugin) {
        instances.put(plugin.getName(), plugin);
    }

    public static TaijituPlugin getPlugin(String name) {
        return instances.get(name);
    }
}
