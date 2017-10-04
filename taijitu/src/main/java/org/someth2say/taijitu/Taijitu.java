package org.someth2say.taijitu;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.someth2say.taijitu.commons.FileUtil;
import org.someth2say.taijitu.commons.LogUtils;
import org.someth2say.taijitu.compare.ComparisonResult;
import org.someth2say.taijitu.config.ComparisonConfig;
import org.someth2say.taijitu.config.ConfigurationLabels;
import org.someth2say.taijitu.config.TaijituConfig;
import org.someth2say.taijitu.config.TaijituConfigImpl;
import org.someth2say.taijitu.plugins.PluginRegistry;
import org.someth2say.taijitu.plugins.TaijituPlugin;
import org.someth2say.taijitu.query.QueryUtilsException;
import org.someth2say.taijitu.query.database.ConnectionManager;
import org.someth2say.taijitu.query.database.IConnectionFactory;
import org.someth2say.taijitu.query.database.PropertiesBasedConnectionFactory;
import org.someth2say.taijitu.query.properties.HProperties;
import org.someth2say.taijitu.strategy.ComparisonStrategyRegistry;

import static org.someth2say.taijitu.config.DefaultConfig.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author Jordi Sola
 */
public final class Taijitu {

    private static final Logger logger = Logger.getLogger(Taijitu.class);


    /*
     * Avoids creation of new objects
     * Static methods should be used
     */
    private Taijitu() {
    }

    public TaijituConfig initialise(final String configProperties) throws TaijituException {
        TaijituConfig config = TaijituConfigImpl.fromFile(configProperties);
        performSetup(config);
        return config;
    }

    private void performSetup(final TaijituConfig config) {
        setupFolders(config);
        setupLogging(config);
        setupRegistries(config);
    }

    private void setupRegistries(final TaijituConfig config) {
        if (config.isUseScanClassPath()) {
            PluginRegistry.scanClassPath();
            ComparisonStrategyRegistry.scanClassPath();

        } else {
            PluginRegistry.useDefaults();
            ComparisonStrategyRegistry.useDefaults();
        }
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

    public Collection<ComparisonResult> compare() throws TaijituException {
        return compare(DEFAULT_CONFIG_FILE);
    }

    public Collection<ComparisonResult> compare(final String configProperties) throws TaijituException {
        TaijituConfig config = initialise(configProperties);
        return performComparisons(config);
    }

    private Collection<ComparisonResult> performComparisons(final TaijituConfig config) throws TaijituException {
        logger.info("Start comparisons.");

        ComparisonConfig[] comparisonConfigs = config.getComparisons();


        // Move to lazy plugin initialization
        // final List<TaijituPlugin> plugins = PluginRegistry.getPlugins(config.getAllPlugins());
        startPlugins(plugins);
               
        // Move to lazy database connection initialization
        final ExecutorService threadPool = Executors.newFixedThreadPool(config.getThreads());
        // IConnectionFactory connectionFactory = new PropertiesBasedConnectionFactory(TaijituConfigImpl.getDatabaseProperties(), ConfigurationLabels.DATABASE_SECTION);

        final Collection<TaijituThread> threads = runComparisonThreads(threadPool, connectionFactory);

        // Wait for finalisation
        waitForFinalisation(threadPool);

        // Close all open connections generated by comparison queries:
        ConnectionPool.closeConnections();

        // Collect results
        final Collection<ComparisonResult> result = getComparisonResults(threads);

        //Should use plug-in registry.
        PluginRegistry.endAllPlugins();

        return result;

    }
    

    private Collection<Future<ComparisonResult>> runComparisonThreads(final ExecutorService threadPool, IConnectionFactory connectionFactory, final ComparisonConfig[] comparisonConfigs) {

        Collection<Future<ComparisonResult>> result = new ArrayList<>(comparisonConfigs.length);
        
        for (ComparisonConfig comparisonConfig : comparisonConfigs) {

            // Run single comparison
            try {
                // Obtain runntime objects based on config: queries/connections/comparators...
                final TaijituData taijituData = new TaijituData(comparisonConfig);

                
                final TaijituThread taijituThread = new TaijituThread(taijituData);
                
                Future<ComparisonResult> futureResult = threadPool.submit(taijituThread);
                
                result.add(futureResult);

                
            } catch (final TaijituException e) {
                logger.error("Error while creating comparison: " + comparisonName + "\n Please review properties.", e);
            }
        }

        return result;
    }
    

    private void endPlugins(List<TaijituPlugin> plugins) throws TaijituException {
        for (int pluginIdx = plugins.size() - 1; pluginIdx >= 0; pluginIdx--) {
            TaijituPlugin plugin = plugins.get(pluginIdx);
            plugin.end();
        }
    }

    private void startPlugins(List<TaijituPlugin> plugins) throws TaijituException {
        for (int pluginIdx = 0, pluginsSize = plugins.size(); pluginIdx < pluginsSize; pluginIdx++) {
            TaijituPlugin plugin = plugins.get(pluginIdx);
            plugin.start();
        }
    }

    private Collection<ComparisonResult> getComparisonResults(Collection<TaijituThread> threads) {
        final Collection<ComparisonResult> result = new ArrayList<>();
        for (final TaijituThread thread : threads) {
            result.add(thread.getComparison().getResult());
        }
        return result;
    }

    private void closeConnections() {
        try {
            ConnectionManager.closeConnections();
        } catch (QueryUtilsException e) {
            logger.error("Error while closing connections (will continue)" + e.getMessage(), e);
        }
    }

    private void waitForFinalisation(ExecutorService threadPool) throws TaijituException {
        threadPool.shutdown();
        try {
            while (!threadPool.awaitTermination(10, TimeUnit.SECONDS)) {
                logger.debug("Awaiting for thread pool finalization.");
            }
        } catch (final InterruptedException t) {
            throw new TaijituException("Comparison terminated unexpectedly: " + t.getMessage(), t);
        }
    }

}
