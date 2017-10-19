package org.someth2say.taijitu.registry;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.config.PluginConfig;
import org.someth2say.taijitu.plugins.TaijituPlugin;
import org.someth2say.taijitu.plugins.logging.TimeLoggingPlugin;
import org.someth2say.taijitu.util.ClassScanUtils;

import java.util.Collection;
import java.util.HashMap;
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

	public static Collection<TaijituPlugin> getAllPlugins() {
		// Do we actually need plugins to be in some order?
		return instances.values();
	}

	public static Map<PluginConfig, TaijituPlugin> getPlugins(PluginConfig[] pluginConfigs) {
		Map<PluginConfig, TaijituPlugin> result = new HashMap<>(pluginConfigs.length);
		for (PluginConfig pluginConfig : pluginConfigs) {
			final TaijituPlugin pluginInstance = instances.get(pluginConfig.getName());
			if (pluginInstance == null) {
				logger.warn("Plugin reference" + pluginConfig.getName() + " not available.");
			} else {
				result.put(pluginConfig, pluginInstance);
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
//		addPlugin(new XLSWriterPlugin());
//		addPlugin(new CSVWriterPlugin());
		addPlugin(new TimeLoggingPlugin());
	}

	private static TaijituPlugin addPlugin(TaijituPlugin plugin) {
		return instances.put(plugin.getName(), plugin);
	}

	public static TaijituPlugin getPlugin(String name) {
		return instances.get(name);
	}
}
