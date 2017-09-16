package org.someth2say.taijitu.plugins;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.plugins.logging.TimeLoggingPlugin;
import org.someth2say.taijitu.plugins.reporting.CSVWriterPlugin;
import org.someth2say.taijitu.plugins.reporting.XLSWriterPlugin;
import org.someth2say.taijitu.util.ClassScanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Jordi Sola on 22/02/2017.
 */
public class PluginRegistry {

    private static final Logger logger = Logger.getLogger(PluginRegistry.class);
    private static Map<String, TaijituPlugin> instances;

    private PluginRegistry() {
    }

    public static List<TaijituPlugin> getPlugins(String[] plugins) {
        List<TaijituPlugin> result = new ArrayList<>(plugins.length);
        for (String plugin : plugins) {
            final TaijituPlugin pluginInstance = instances.get(plugin);
            if (pluginInstance == null) {
                logger.warn("Plugin " + plugin + " not available.");
            } else {
                result.add(pluginInstance);
            }
        }
        return result;
    }

    public static void scanClassPath() {
        // This seems fast enough for a one-shot initialization
        // If found slow, it can be changed to scan only sub-packages for taijitu
        final Class<TaijituPlugin> implementedInterface = TaijituPlugin.class;
        instances = ClassScanUtils.getInstancesForClassesImplementing(implementedInterface);
        logger.info("Registered plugins: " + instances.keySet().toString());
    }

    public static void useDefaults() {
        instances = new ConcurrentHashMap<>();
        addPlugin(new XLSWriterPlugin());
        addPlugin(new CSVWriterPlugin());
        addPlugin(new TimeLoggingPlugin());
    }

    private static TaijituPlugin addPlugin(TaijituPlugin xls) {
        return instances.put(xls.getName(), xls);
    }
}
