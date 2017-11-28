package org.someth2say.taijitu;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.compare.result.Mismatch;
import org.someth2say.taijitu.ui.config.impl.TaijituCfg;
import org.someth2say.taijitu.ui.config.interfaces.IComparisonCfg;
import org.someth2say.taijitu.ui.config.interfaces.IPluginCfg;
import org.someth2say.taijitu.ui.config.interfaces.ITaijituCfg;
import org.someth2say.taijitu.ui.plugins.TaijituPlugin;
import org.someth2say.taijitu.ui.registry.*;
import org.someth2say.taijitu.ui.source.query.ConnectionManager;
import org.someth2say.taijitu.util.FileUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.someth2say.taijitu.ui.config.DefaultConfig.DEFAULT_CONFIG_FILE;

/**
 * @author Jordi Sola
 */
public final class Taijitu {

    private static final Logger logger = LoggerFactory.getLogger(Taijitu.class);

    private static ITaijituCfg configFromApache(final ImmutableHierarchicalConfiguration properties) {
        return TaijituCfg.fromApacheConfig(properties);
    }


    private static ITaijituCfg configFromFile(final String fileName) throws TaijituException {
        return TaijituCfg.fromFile(fileName);
    }

    private static void performSetup(final ITaijituCfg config) throws TaijituException {
        try {
            setupRegistries(config);
        } catch (Exception e) {
            throw new TaijituException("Unable to prepare Taijitu.", e);
        }
    }

    //TODO: This registry stuff may be moved to a IC context (Weld?)
    private static void setupRegistries(final ITaijituCfg config) {
        if (config.isUseScanClassPath()) {
            PluginRegistry.scanClassPath();
            StreamEqualityRegistry.scanClassPath();
            ValueEqualityRegistry.scanClassPath();
            SourceRegistry.scanClassPath();
            MapperRegistry.scanClassPath();
        } else {
            PluginRegistry.useDefaults();
            StreamEqualityRegistry.useDefaults();
            ValueEqualityRegistry.useDefaults();
            SourceRegistry.useDefaults();
            MapperRegistry.useDefaults();
        }
    }

    public static void main(final String[] args) {
        if (args.length != 1) {
            FileUtil.dumpResource("usage-taijitu.txt");
        } else {
            // run comparison with default values
            try {
                Taijitu.compare(args[0]);
            } catch (TaijituException e) {
                logger.error("Unable to start: ", e);
            }
        }
    }

    public static List<List<Mismatch>> compare() throws TaijituException {
        return compare(DEFAULT_CONFIG_FILE);
    }

    private static List<List<Mismatch>> compare(final String fileName) throws TaijituException {
        return compare(configFromFile(fileName));
    }


    public static List<List<Mismatch>> compare(final ImmutableHierarchicalConfiguration properties) throws TaijituException {
        return compare(configFromApache(properties));
    }


    public static List<List<Mismatch>> compare(final ITaijituCfg config) throws TaijituException {
        logger.info("Start comparisons.");
        performSetup(config);

        List<IComparisonCfg> comparisons = config.getComparisons();

        startPlugins(config);

        final CompletionService<List<Mismatch>> completionService = runComparisons(config);

        // Collect results
        final List<List<Mismatch>> result = getComparisonResults(completionService, comparisons);

        endPlugins(config);

        // Close all open connections generated by comparison queries:
        closeDataSources();

        return result;

    }

    private static CompletionService<List<Mismatch>> runComparisons(final ITaijituCfg config) {
        final ExecutorService executorService = Executors.newFixedThreadPool(config.getThreads());
        CompletionService<List<Mismatch>> completionService = new ExecutorCompletionService<>(executorService);

        config.getComparisons().forEach(comparison -> completionService.submit(new TaijituRunner(comparison)));

        executorService.shutdown();

        return completionService;
    }


    private static void endPlugins(ITaijituCfg config) {
        List<IPluginCfg> allPluginsConfig = config.getPluginConfigs();
        for (IPluginCfg pluginConfigIface : allPluginsConfig) {
            TaijituPlugin plugin = PluginRegistry.getPlugin(pluginConfigIface.getName());
            plugin.end(pluginConfigIface);
        }
    }

    private static void startPlugins(ITaijituCfg config) {
        List<IPluginCfg> allPluginsConfig = config.getPluginConfigs();
        for (IPluginCfg pluginCfg : allPluginsConfig) {
            TaijituPlugin plugin = PluginRegistry.getPlugin(pluginCfg.getName());
            plugin.start(pluginCfg);
        }
    }

    private static List<List<Mismatch>> getComparisonResults(CompletionService<List<Mismatch>> completionService,
                                                             List<IComparisonCfg> iComparisonCfgs) {
        final List<List<Mismatch>> result = new ArrayList<>(iComparisonCfgs.size());

        for (int i = 0; i < iComparisonCfgs.size(); i++) {
            try {
                Future<List<Mismatch>> future = completionService.take();
                try {
                    result.add(future.get());
                } catch (ExecutionException e) {
                    logger.error("Unable to obtain comparison result.", e);
                    result.add(null);
                }
            } catch (InterruptedException e) {
                logger.error("Unable to obtain comparison result.", e);
                result.add(null);
            }
        }
        return result;
    }

    private static void closeDataSources() {
        ConnectionManager.closeAllDataSources();
    }

}
