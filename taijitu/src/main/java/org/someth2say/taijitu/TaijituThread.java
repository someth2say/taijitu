package org.someth2say.taijitu;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.compare.ComparisonResult;
import org.someth2say.taijitu.config.ComparisonConfig;
import org.someth2say.taijitu.config.ComparisonPluginConfig;
import org.someth2say.taijitu.plugins.PluginRegistry;
import org.someth2say.taijitu.plugins.TaijituPlugin;

/**
 * @author Jordi Sola
 */
public class TaijituThread implements Callable<ComparisonResult> {

	private static final Logger logger = Logger.getLogger(TaijituThread.class);

	private final ComparisonConfig comparisonConfig;
	private final ComparisonResult result;
	private final ComparisonRuntime comparison;

	public TaijituThread(final ComparisonConfig comparisonConfig) throws TaijituException {
		this.comparisonConfig = comparisonConfig;
		result = new ComparisonResult(comparisonConfig);
		comparison = new ComparisonRuntime(comparisonConfig);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Runnable#run()
	 */
	public ComparisonResult call() {

		try {

			result.setStatus(ComparisonResult.ComparisonResultStatus.RUNNING);

			Map<ComparisonPluginConfig, TaijituPlugin> plugins = PluginRegistry
					.getPlugins(comparisonConfig.getComparisonPluginConfigs());

			runPluginsPreComparison(comparison, plugins);

			runComparisonStrategy();

			runPluginsPostComparison(comparison, plugins);

			result.setStatus(ComparisonResult.ComparisonResultStatus.SUCCESS);

		} catch (final TaijituException e) {
			logger.error(e.getMessage(), e);
			result.setStatus(ComparisonResult.ComparisonResultStatus.ERROR);
		}

		return result;
	}

	private void runPluginsPostComparison(final ComparisonRuntime comparison,
			Map<ComparisonPluginConfig, TaijituPlugin> plugins) throws TaijituException {
		for (Entry<ComparisonPluginConfig, TaijituPlugin> entry : plugins.entrySet()) {
			entry.getValue().postComparison(comparison, entry.getKey());
		}
	}

	private void runPluginsPreComparison(final ComparisonRuntime comparison,
			Map<ComparisonPluginConfig, TaijituPlugin> plugins) throws TaijituException {
		for (Entry<ComparisonPluginConfig, TaijituPlugin> entry : plugins.entrySet()) {
			entry.getValue().preComparison(comparison, entry.getKey());
		}
	}

	private void runComparisonStrategy() throws TaijituException {
		// Show comparison description
		logger.info(
				"COMPARISON: " + comparison.getTestName() + "(strategy " + comparison.getStrategy().getName() + ")");
		// logger.debug("SETUP: " +
		// comparisonConfig.getAllSetup(comparison.getTestName()).toString());
		logger.debug("PARAMETERS: " + comparisonConfig.getAllParameters());

		comparison.getStrategy().runComparison(comparison, comparisonConfig);
	}

}
