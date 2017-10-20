package org.someth2say.taijitu;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.someth2say.taijitu.compare.ComparisonResult;
import org.someth2say.taijitu.config.ComparisonConfig;
import org.someth2say.taijitu.config.PluginConfig;
import org.someth2say.taijitu.config.DatabaseConfig;
import org.someth2say.taijitu.config.TaijituConfig;
import org.someth2say.taijitu.config.impl.TaijituConfigImpl;
import org.someth2say.taijitu.database.ConnectionManager;
import org.someth2say.taijitu.database.QueryUtilsException;
import org.someth2say.taijitu.plugins.TaijituPlugin;
import org.someth2say.taijitu.registry.ComparisonStrategyRegistry;
import org.someth2say.taijitu.registry.EqualityStrategyRegistry;
import org.someth2say.taijitu.registry.MatcherRegistry;
import org.someth2say.taijitu.registry.PluginRegistry;
import org.someth2say.taijitu.util.FileUtil;
import org.someth2say.taijitu.util.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.*;

import static org.someth2say.taijitu.config.DefaultConfig.DEFAULT_CONFIG_FILE;
import static org.someth2say.taijitu.config.DefaultConfig.DEFAULT_LOG_FILE;

/**
 * @author Jordi Sola
 */
public final class Taijitu {

    private static final Logger logger = Logger.getLogger(Taijitu.class);

    public Taijitu() {
    }


    private TaijituConfig initialise(final ImmutableHierarchicalConfiguration properties) throws TaijituException {
        TaijituConfig config = TaijituConfigImpl.fromProperties(properties);
        performSetup(config);
        return config;
    }


    private TaijituConfig initialise(final String configProperties) throws TaijituException {
        TaijituConfig config = TaijituConfigImpl.fromFile(configProperties);
        performSetup(config);
        return config;
    }

    private void performSetup(final TaijituConfig config) {
        setupFolders(config);
        setupLogging(config);
        setupRegistries(config);
    }

    //TODO: This registry stuff may be moved to a IC context (Weld?)
    private void setupRegistries(final TaijituConfig config) {
        if (config.isUseScanClassPath()) {
            PluginRegistry.scanClassPath();
            ComparisonStrategyRegistry.scanClassPath();
            MatcherRegistry.scanClassPath();
            EqualityStrategyRegistry.scanClassPath();
        } else {
            PluginRegistry.useDefaults();
            ComparisonStrategyRegistry.useDefaults();
            MatcherRegistry.useDefaults();
            EqualityStrategyRegistry.useDefaults();
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

    public ComparisonResult[] compare() throws TaijituException {
        return compare(DEFAULT_CONFIG_FILE);
    }

    public ComparisonResult[] compare(final String configProperties) throws TaijituException {
        TaijituConfig config = initialise(configProperties);
        return performComparisons(config);
    }


    public ComparisonResult[] compare(final ImmutableHierarchicalConfiguration properties) throws TaijituException {
        TaijituConfig config = initialise(properties);
        return performComparisons(config);
    }


    private ComparisonResult[] performComparisons(final TaijituConfig config) throws TaijituException {
        logger.info("Start comparisons.");

        ComparisonConfig[] comparisonConfigs = config.getComparisons();

        startDataSources(config);

        startPlugins(config);

        final CompletionService<ComparisonResult> completionService = runComparisons(config);

        // Collect results
        final ComparisonResult[] result = getComparisonResults(completionService, comparisonConfigs);

        endPlugins(config);

        // Close all open connections generated by comparison queries:
        closeDataSources();

        return result;

    }

    private CompletionService<ComparisonResult> runComparisons(final TaijituConfig config) throws TaijituException {
        final ExecutorService executorService = Executors.newFixedThreadPool(config.getThreads());
        CompletionService<ComparisonResult> completionService = new ExecutorCompletionService<>(executorService);

        //final Collection<Future<ComparisonResult>> futures =
        runComparisonThreads(completionService, config);

        // Shutdown: no more tasks allowed.
        executorService.shutdown();

        return completionService;
    }


    private void endPlugins(TaijituConfig config) throws TaijituException {
        PluginConfig[] allPluginsConfig = config.getComparisonPluginConfigs();
        for (PluginConfig pluginConfig : allPluginsConfig) {
            TaijituPlugin plugin = PluginRegistry.getPlugin(pluginConfig.getName());
            plugin.end(pluginConfig);
        }
    }

    private void startPlugins(TaijituConfig config) throws TaijituException {
        PluginConfig[] allPluginsConfig = config.getComparisonPluginConfigs();
        for (PluginConfig pluginConfig : allPluginsConfig) {
            TaijituPlugin plugin = PluginRegistry.getPlugin(pluginConfig.getName());
            plugin.start(pluginConfig);
        }
    }

    private ComparisonResult[] getComparisonResults(CompletionService<ComparisonResult> completionService,
                                                    ComparisonConfig[] comparisonConfigs) {
        final ComparisonResult[] result = new ComparisonResult[comparisonConfigs.length];
        for (int i = 0; i < comparisonConfigs.length; i++) {
            try {
                Future<ComparisonResult> future = completionService.take();
                try {
                    result[i] = future.get();
                } catch (ExecutionException e) {
                    logger.error("Unable to obtain comparison result.",e);
                    result[i] = null;
                }
            } catch (InterruptedException e) {
                logger.error("Unable to obtain comparison result.",e);
                result[i] = null;
            }
        }
        return result;
    }

    private void startDataSources(TaijituConfig config) {
        DatabaseConfig[] dbConfigs = config.getAllDatabaseConfigs();
        for (DatabaseConfig databaseConfig : dbConfigs) {
            Properties dbProperties = databaseConfig.getDatabaseProperties();
            ConnectionManager.buildDataSource(databaseConfig.getName(), dbProperties);
        }
    }

    private void closeDataSources() {
        try {
            ConnectionManager.closeAllDataSources();
        } catch (QueryUtilsException e) {
            logger.error("Error while closing connections (will continue)" + e.getMessage(), e);
        }
    }

    private Collection<Future<ComparisonResult>> runComparisonThreads(
            final CompletionService<ComparisonResult> completionService, final TaijituConfig config) {

        final ComparisonConfig[] comparisonConfigs = config.getComparisons();
        final Collection<Future<ComparisonResult>> result = new ArrayList<>(comparisonConfigs.length);

        for (ComparisonConfig comparisonConfig : comparisonConfigs) {

            try {
                final TaijituRunner taijituRunner = new TaijituRunner(comparisonConfig);

                Future<ComparisonResult> future = completionService.submit(taijituRunner);

                result.add(future);

            } catch (final TaijituException e) {
                logger.error("Error while creating comparison: " + comparisonConfig + "\n Please review config.", e);
            }
        }

        return result;
    }

    private void setupFolders(final TaijituConfig config) {
        final File outputFolder = new File(config.getOutputFolder());
        if (!outputFolder.exists()) {
            final boolean dirCreated = outputFolder.mkdirs();
            if (!dirCreated) {
                logger.error("Error while trying to create output folder: " + outputFolder.getAbsolutePath());
            }
        }
    }

    private void setupLogging(final TaijituConfig config) {
        enableFileLog(config);
        enableConsoleLog(config);
    }

    private void enableFileLog(final TaijituConfig config) {
        final Level level = Level.toLevel(config.getFileLog(), Level.OFF);
        if (level != Level.OFF) {
            final String fileName = config.getOutputFolder() + File.separator + DEFAULT_LOG_FILE;
            LogUtils.addFileAppenderToRootLogger(level, LogUtils.DEFAULT_PATTERN, fileName);
        }
    }

    private void enableConsoleLog(final TaijituConfig config) {
        final Level level = Level.toLevel(config.getConsoleLog(), Level.INFO);
        LogUtils.addConsoleAppenderToRootLogger(level, LogUtils.DEFAULT_PATTERN);
    }

}
