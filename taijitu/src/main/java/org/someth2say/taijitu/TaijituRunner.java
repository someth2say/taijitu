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
import org.someth2say.taijitu.query.QueryUtilsException;
import org.someth2say.taijitu.strategy.ComparisonStrategy;
import org.someth2say.taijitu.strategy.ComparisonStrategyRegistry;

/**
 * @author Jordi Sola
 */
public class TaijituRunner implements Callable<ComparisonResult> {

    private static final Logger logger = Logger.getLogger(TaijituRunner.class);

    private final ComparisonConfig config;
    private final ComparisonResult result;
    private final ComparisonRuntime comparison;

    public TaijituRunner(final ComparisonConfig config) throws TaijituException, QueryUtilsException {
        this.config = config;
        result = new ComparisonResult(config);
        comparison = new ComparisonRuntime(config);
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
                    .getPlugins(config.getComparisonPluginConfigs());

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
        logger.info("COMPARISON: " + config.getName() + "(strategy " + config.getStrategyConfig().getName() + ")");
        logger.debug("PARAMETERS: " + config.getAllParameters());

        final ComparisonStrategy strategy = ComparisonStrategyRegistry.getStrategy(config.getStrategyConfig());
        strategy.runComparison(comparison, config);
    }

}
