package org.someth2say.taijitu;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.someth2say.taijitu.compare.result.ComparisonResult;
import org.someth2say.taijitu.config.impl.TaijituCfg;
import org.someth2say.taijitu.config.interfaces.IComparisonCfg;
import org.someth2say.taijitu.config.interfaces.IPluginCfg;
import org.someth2say.taijitu.config.interfaces.ITaijituCfg;
import org.someth2say.taijitu.source.query.ConnectionManager;
import org.someth2say.taijitu.plugins.TaijituPlugin;
import org.someth2say.taijitu.registry.*;
import org.someth2say.taijitu.util.FileUtil;
import org.someth2say.taijitu.util.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

import static org.someth2say.taijitu.config.DefaultConfig.DEFAULT_CONFIG_FILE;
import static org.someth2say.taijitu.config.DefaultConfig.DEFAULT_LOG_FILE;

/**
 * @author Jordi Sola
 */
public final class Taijitu {

    private static final Logger logger = Logger.getLogger(Taijitu.class);

    private static ITaijituCfg configFromApache(final ImmutableHierarchicalConfiguration properties) throws TaijituException {
        return TaijituCfg.fromApacheConfig(properties);
    }


    private static ITaijituCfg configFromFile(final String fileName) throws TaijituException {
        return TaijituCfg.fromFile(fileName);
    }

    private static void performSetup(final ITaijituCfg config) {
        setupFolders(config);
        setupLogging(config);
        setupRegistries(config);
    }

    //TODO: This registry stuff may be moved to a IC context (Weld?)
    private static void setupRegistries(final ITaijituCfg config) {
        if (config.isUseScanClassPath()) {
            PluginRegistry.scanClassPath();
            StreamEqualityRegistry.scanClassPath();
            MatcherRegistry.scanClassPath();
            ValueEqualityRegistry.scanClassPath();
            SourceRegistry.scanClassPath();
        } else {
            PluginRegistry.useDefaults();
            StreamEqualityRegistry.useDefaults();
            MatcherRegistry.useDefaults();
            ValueEqualityRegistry.useDefaults();
            SourceRegistry.useDefaults();
        }
    }

    public static void main(final String[] args) {
        if (args.length != 1) {
            FileUtil.dumpResource("usage-taijitu.txt");
        } else {
            // run comparison with default values
            try {
                new Taijitu().compare(args[0]);
            } catch (TaijituException e) {
                logger.fatal("Unable to start: ", e);
            }
        }
    }

    public static ComparisonResult[] compare() throws TaijituException {
        return compare(DEFAULT_CONFIG_FILE);
    }

    public static ComparisonResult[] compare(final String fileName) throws TaijituException {
        return compare(configFromFile(fileName));
    }


    public static ComparisonResult[] compare(final ImmutableHierarchicalConfiguration properties) throws TaijituException {
        return compare(configFromApache(properties));
    }


    public static ComparisonResult[] compare(final ITaijituCfg config) throws TaijituException {
        logger.info("Start comparisons.");
        performSetup(config);

        List<IComparisonCfg> comparisons = config.getComparisons();

        startPlugins(config);

        final CompletionService<ComparisonResult> completionService = runComparisons(config);

        // Collect results
        final ComparisonResult[] result = getComparisonResults(completionService, comparisons);

        endPlugins(config);

        // Close all open connections generated by comparison queries:
        closeDataSources();

        return result;

    }

    private static CompletionService<ComparisonResult> runComparisons(final ITaijituCfg config) throws TaijituException {
        final ExecutorService executorService = Executors.newFixedThreadPool(config.getThreads());
        CompletionService<ComparisonResult> completionService = new ExecutorCompletionService<>(executorService);

        //final Collection<Future<ComparisonResult>> futures =
        runComparisonThreads(completionService, config);

        // Shutdown: no more tasks allowed.
        executorService.shutdown();

        return completionService;
    }


    private static void endPlugins(ITaijituCfg config) throws TaijituException {
        List<IPluginCfg> allPluginsConfig = config.getComparisonPluginConfigs();
        for (IPluginCfg pluginConfigIface : allPluginsConfig) {
            TaijituPlugin plugin = PluginRegistry.getPlugin(pluginConfigIface.getName());
            plugin.end(pluginConfigIface);
        }
    }

    private static void startPlugins(ITaijituCfg config) throws TaijituException {
        List<IPluginCfg> allPluginsConfig = config.getComparisonPluginConfigs();
        for (IPluginCfg pluginCfg : allPluginsConfig) {
            TaijituPlugin plugin = PluginRegistry.getPlugin(pluginCfg.getName());
            plugin.start(pluginCfg);
        }
    }

    private static ComparisonResult[] getComparisonResults(CompletionService<ComparisonResult> completionService,
                                                           List<IComparisonCfg> iComparisonCfgs) {
        final ComparisonResult[] result = new ComparisonResult[iComparisonCfgs.size()];
        for (int i = 0; i < iComparisonCfgs.size(); i++) {
            try {
                Future<ComparisonResult> future = completionService.take();
                try {
                    result[i] = future.get();
                } catch (ExecutionException e) {
                    logger.error("Unable to obtain comparison result.", e);
                    result[i] = null;
                }
            } catch (InterruptedException e) {
                logger.error("Unable to obtain comparison result.", e);
                result[i] = null;
            }
        }
        return result;
    }

    private static void closeDataSources() {
        ConnectionManager.closeAllDataSources();
    }

    private static Collection<Future<ComparisonResult>> runComparisonThreads(
            final CompletionService<ComparisonResult> completionService, final ITaijituCfg config) {

        final List<IComparisonCfg> comparisons = config.getComparisons();
        final Collection<Future<ComparisonResult>> result = new ArrayList<>(comparisons.size());

        comparisons.forEach(comparison -> {
            try {
                final TaijituRunner taijituRunner = new TaijituRunner(comparison);

                Future<ComparisonResult> future = completionService.submit(taijituRunner);

                result.add(future);

            } catch (final TaijituException e) {
                logger.error("Error while creating comparison: " + comparison + "\n Please review config.", e);
            }
        });

        return result;
    }

    private static void setupFolders(final ITaijituCfg config) {
        final File outputFolder = new File(config.getOutputFolder());
        if (!outputFolder.exists()) {
            final boolean dirCreated = outputFolder.mkdirs();
            if (!dirCreated) {
                logger.error("Error while trying to create output folder: " + outputFolder.getAbsolutePath());
            }
        }
    }

    private static void setupLogging(final ITaijituCfg config) {
        enableFileLog(config);
        enableConsoleLog(config);
    }

    private static void enableFileLog(final ITaijituCfg config) {
        final Level level = Level.toLevel(config.getFileLog(), Level.OFF);
        if (level != Level.OFF) {
            final String fileName = config.getOutputFolder() + File.separator + DEFAULT_LOG_FILE;
            LogUtils.addFileAppenderToRootLogger(level, LogUtils.DEFAULT_PATTERN, fileName);
        }
    }

    private static void enableConsoleLog(final ITaijituCfg config) {
        final Level level = Level.toLevel(config.getConsoleLog(), Level.INFO);
        LogUtils.addConsoleAppenderToRootLogger(level, LogUtils.DEFAULT_PATTERN);
    }

}
