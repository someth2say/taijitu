package org.someth2say.taijitu;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.compare.ComparisonResult;
import org.someth2say.taijitu.config.TaijituConfig;
import org.someth2say.taijitu.plugins.TaijituPlugin;

import java.util.List;

/**
 * @author Jordi Sola
 */
public class TaijituThread implements Runnable {

    private static final Logger logger = Logger.getLogger(TaijituThread.class);

    private final TaijituData comparison;

    public TaijituThread(final TaijituData _comparison) {
        this.comparison = _comparison;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    public void run() {
        final ComparisonResult result = comparison.getResult();
        try {

            result.setStatus(ComparisonResult.ComparisonResultStatus.RUNNING);

            List<TaijituPlugin> plugins = comparison.getPlugins();
            for (int pluginIdx = 0, pluginsSize = plugins.size(); pluginIdx < pluginsSize; pluginIdx++) {
                TaijituPlugin plugin = plugins.get(pluginIdx);
                plugin.preComparison(comparison);
            }

            runComparisonStrategy();

            for (int pluginIdx = plugins.size() - 1; pluginIdx >= 0; pluginIdx--) {
                TaijituPlugin plugin = plugins.get(pluginIdx);
                plugin.postComparison(comparison);
            }

            result.setStatus(ComparisonResult.ComparisonResultStatus.SUCCESS);

        } catch (final TaijituException e) {
            logger.error(e.getMessage(), e);
            result.setStatus(ComparisonResult.ComparisonResultStatus.ERROR);
        }
    }

    private void runComparisonStrategy() throws TaijituException {
        // Show comparison description
        logger.info("COMPARISON: " + comparison.getTestName() + "(strategy " + comparison.getStrategy().getName() + ")");
        logger.debug("SETUP: " + TaijituConfig.getAllSetup(comparison.getTestName()).toString());
        logger.debug("PARAMETERS: " + TaijituConfig.getAllParameters(comparison.getTestName()).toString());

        comparison.getStrategy().runComparison(comparison);
    }


    public TaijituData getComparison() {
        return comparison;
    }

}
