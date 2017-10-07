package org.someth2say.taijitu;

import static org.someth2say.taijitu.config.DefaultConfig.DEFAULT_CONFIG_FILE;
import static org.someth2say.taijitu.config.DefaultConfig.DEFAULT_LOG_FILE;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.someth2say.taijitu.commons.FileUtil;
import org.someth2say.taijitu.commons.LogUtils;
import org.someth2say.taijitu.compare.ComparisonResult;
import org.someth2say.taijitu.config.ComparisonConfig;
import org.someth2say.taijitu.config.ComparisonPluginConfig;
import org.someth2say.taijitu.config.DatabaseConfig;
import org.someth2say.taijitu.config.TaijituConfig;
import org.someth2say.taijitu.config.TaijituConfigImpl;
import org.someth2say.taijitu.plugins.PluginRegistry;
import org.someth2say.taijitu.plugins.TaijituPlugin;
import org.someth2say.taijitu.query.QueryUtilsException;
import org.someth2say.taijitu.query.database.ConnectionManager;
import org.someth2say.taijitu.strategy.ComparisonStrategyRegistry;

/**
 * @author Jordi Sola
 */
public final class Taijitu {

	private static final Logger logger = Logger.getLogger(Taijitu.class);

	/*
	 * Avoids creation of new objects Static methods should be used
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
		CompletionService<ComparisonResult> completionService = new ExecutorCompletionService<ComparisonResult>(
				executorService);

		final Collection<Future<ComparisonResult>> futures = runComparisonThreads(completionService, config);

		// Shutdown: no more tasks allowed.
		executorService.shutdown();

		// waitForFinalization(executorService);

		return completionService;
	}

	@Deprecated
	private void waitForFinalization(final ExecutorService executorService) throws TaijituException {
		// Wait for finalisation
		try {
			while (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
				logger.debug("Awaiting for thread pool finalization.");
			}
		} catch (final InterruptedException t) {
			throw new TaijituException("Comparison terminated unexpectedly: " + t.getMessage(), t);
		}
	}

	private void endPlugins(TaijituConfig config) throws TaijituException {
		ComparisonPluginConfig[] allPluginsConfig = config.getAllPluginsConfig();
		for (int pluginIdx = 0, pluginsSize = allPluginsConfig.length; pluginIdx < pluginsSize; pluginIdx++) {
			ComparisonPluginConfig pluginConfig = allPluginsConfig[pluginIdx];
			TaijituPlugin plugin = PluginRegistry.getPlugin(pluginConfig.getName());
			plugin.end(pluginConfig);
		}
	}

	private void startPlugins(TaijituConfig config) throws TaijituException {
		ComparisonPluginConfig[] allPluginsConfig = config.getAllPluginsConfig();
		for (int pluginIdx = 0, pluginsSize = allPluginsConfig.length; pluginIdx < pluginsSize; pluginIdx++) {
			ComparisonPluginConfig pluginConfig = allPluginsConfig[pluginIdx];
			TaijituPlugin plugin = PluginRegistry.getPlugin(pluginConfig.getName());
			plugin.start(pluginConfig);
		}
	}

	private ComparisonResult[] getComparisonResults(CompletionService<ComparisonResult> completionService,
			ComparisonConfig[] comparisonConfigs) {
		final ComparisonResult[] result = new ComparisonResult[comparisonConfigs.length];
		for (int i = 0; i < comparisonConfigs.length; i++) {
			Future<ComparisonResult> future;
			try {
				future = completionService.take();
				try {
					result[i] = future.get();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return result;
	}

	private void startDataSources(TaijituConfig config) {
		DatabaseConfig[] dbConfigs = config.getAllDatabaseConfigs();
		for (DatabaseConfig databaseConfig : dbConfigs) {
			Properties dbProperties = databaseConfig.getAsProperties();
			ConnectionManager.buildDataSource(databaseConfig.getName(), dbProperties);
		}
	}

	private void closeDataSources() {
		try {
			ConnectionManager.closeAllConnections();
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
				final TaijituThread taijituThread = new TaijituThread(comparisonConfig);

				Future<ComparisonResult> future = completionService.submit(taijituThread);

				result.add(future);

			} catch (final TaijituException e) {
				logger.error("Error while creating comparison: " + comparisonConfig + "\n Please review properties.",
						e);
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
