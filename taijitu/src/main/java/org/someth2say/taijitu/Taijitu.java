package org.someth2say.taijitu;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.someth2say.taijitu.commons.FileUtil;
import org.someth2say.taijitu.commons.LogUtils;
import org.someth2say.taijitu.compare.ComparisonResult;
import org.someth2say.taijitu.config.ConfigurationLabels;
import org.someth2say.taijitu.config.TaijituConfig;
import org.someth2say.taijitu.plugins.PluginRegistry;
import org.someth2say.taijitu.plugins.TaijituPlugin;
import org.someth2say.taijitu.query.QueryUtilsException;
import org.someth2say.taijitu.query.database.ConnectionManager;
import org.someth2say.taijitu.query.database.IConnectionFactory;
import org.someth2say.taijitu.query.database.PropertiesBasedConnectionFactory;
import org.someth2say.taijitu.query.properties.HProperties;
import org.someth2say.taijitu.strategy.ComparisonStrategyRegistry;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Jordi Sola
 */
public final class Taijitu {

    private static final String DEFAULT_LOG_FILE = "taijitu.logger";
    private static final String CONFIG_FILE = "taijitu.properties";
    private static final Logger logger = Logger.getLogger(Taijitu.class);


    /*
     * Avoids creation of new objects
     * Static methods should be used
     */
    private Taijitu() {
    }

    public static void initialise(final String configProperties) throws TaijituException {
        TaijituConfig.setConfigProperties(configProperties);
        performSetup();
    }

    public static void initialise(final HProperties configProperties) {
        TaijituConfig.setProperties(configProperties);
        performSetup();
    }

    private static void performSetup() {
        setupFolders();
        setupLogging();
        setupRegistries();
    }

    private static void setupRegistries() {
        if (TaijituConfig.isUseScanClassPath()) {
            PluginRegistry.scanClassPath();
            ComparisonStrategyRegistry.scanClassPath();

        } else {
            PluginRegistry.useDefaults();
            ComparisonStrategyRegistry.useDefaults();
        }
    }

    public static void main(final String[] args) {
        if (args.length != 1) {
            FileUtil.dumpResource("usage-taijitu.txt");
        } else {
            // run comparison with default values
            try {
                compare(args[0]);
            } catch (TaijituException e) {
                logger.fatal("Unable to start: ", e);
            }
        }
    }

    public static Collection<ComparisonResult> compare() throws TaijituException {
        return compare(CONFIG_FILE);
    }

    public static Collection<ComparisonResult> compare(final String configProperties) throws TaijituException {
        initialise(configProperties);
        return performComparisons();
    }

    public static Collection<ComparisonResult> compare(final HProperties configProperties) throws TaijituException {
        initialise(configProperties);
        return performComparisons();
    }

    private static Collection<ComparisonResult> performComparisons() throws TaijituException {
        logger.info("Start comparisons.");

        final List<TaijituPlugin> plugins = PluginRegistry.getPlugins(TaijituConfig.getAllPlugins());
        startPlugins(plugins);

        final ExecutorService threadPool = Executors.newFixedThreadPool(TaijituConfig.getThreads());
        IConnectionFactory connectionFactory = new PropertiesBasedConnectionFactory(TaijituConfig.getDatabaseProperties(), ConfigurationLabels.DATABASE_SECTION);
        final Collection<TaijituThread> threads = runComparisonThreads(threadPool, connectionFactory);

        // Wait for finalisation
        waitForFinalisation(threadPool);

        // Close all open connections generated by comparison queries:
        closeConnections();

        // Collect results
        final Collection<ComparisonResult> result = getComparisonResults(threads);

        endPlugins(plugins);

        return result;

    }

    private static void endPlugins(List<TaijituPlugin> plugins) throws TaijituException {
        for (int pluginIdx = plugins.size() - 1; pluginIdx >= 0; pluginIdx--) {
            TaijituPlugin plugin = plugins.get(pluginIdx);
            plugin.end();
        }
    }

    private static void startPlugins(List<TaijituPlugin> plugins) throws TaijituException {
        for (int pluginIdx = 0, pluginsSize = plugins.size(); pluginIdx < pluginsSize; pluginIdx++) {
            TaijituPlugin plugin = plugins.get(pluginIdx);
            plugin.start();
        }
    }

    private static Collection<ComparisonResult> getComparisonResults(Collection<TaijituThread> threads) {
        final Collection<ComparisonResult> result = new ArrayList<>();
        for (final TaijituThread thread : threads) {
            result.add(thread.getComparison().getResult());
        }
        return result;
    }

    private static void closeConnections() {
        try {
            ConnectionManager.closeConnections();
        } catch (QueryUtilsException e) {
            logger.error("Error while closing connections (will continue)" + e.getMessage(), e);
        }
    }

    private static void waitForFinalisation(ExecutorService threadPool) throws TaijituException {
        threadPool.shutdown();
        try {
            while (!threadPool.awaitTermination(10, TimeUnit.SECONDS)) {
                logger.debug("Awaiting for thread pool finalization.");
            }
        } catch (final InterruptedException t) {
            throw new TaijituException("Comparison terminated unexpectedly: " + t.getMessage(), t);
        }
    }

    private static Collection<TaijituThread> runComparisonThreads(final ExecutorService threadPool, IConnectionFactory connectionFactory) {

        final Set<String> comparisonNames = TaijituConfig.getComparisonNames();

        final Collection<TaijituThread> result = new ArrayList<>(comparisonNames.size());

        for (String comparisonName : comparisonNames) {

            // Run single comparison
            try {
                final TaijituData taijituData = new TaijituData(comparisonName, connectionFactory);
                final TaijituThread TaijituThread = new TaijituThread(taijituData);

                result.add(TaijituThread);
                threadPool.execute(TaijituThread);
            } catch (final TaijituException e) {
                logger.error("Error while creating comparison: " + comparisonName + "\n Please review properties.", e);
            }
        }

        return result;
    }

    private static void setupFolders() {
        final File outputFolder = TaijituConfig.getOutputFolderFile();
        if (!outputFolder.exists()) {
            final boolean dirCreated = outputFolder.mkdirs();
            if (!dirCreated) {
                logger.error("Error while trying to create output folder: " + outputFolder.getAbsolutePath());
            }
        }
    }

    private static void setupLogging() {
        enableFileLog();
        enableConsoleLog();
    }

    private static void enableFileLog() {
        final Level level = Level.toLevel(TaijituConfig.getFileLog(), Level.OFF);
        if (level != Level.OFF) {
            final String fileName = TaijituConfig.getOutputFolderFile() + File.separator + Taijitu.DEFAULT_LOG_FILE;
            LogUtils.addFileAppenderToRootLogger(level, LogUtils.DEFAULT_PATTERN, fileName);
        }
    }

    private static void enableConsoleLog() {
        final Level level = Level.toLevel(TaijituConfig.getConsoleLog(), Level.INFO);
        LogUtils.addConsoleAppenderToRootLogger(level, LogUtils.DEFAULT_PATTERN);
    }

}
