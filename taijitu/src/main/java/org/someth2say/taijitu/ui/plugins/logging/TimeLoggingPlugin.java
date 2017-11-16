package org.someth2say.taijitu.ui.plugins.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;
import org.someth2say.taijitu.ui.config.interfaces.IComparisonCfg;
import org.someth2say.taijitu.ui.config.interfaces.IPluginCfg;
import org.someth2say.taijitu.ui.plugins.TaijituPlugin;

/**
 * Created by Jordi Sola on 24/02/2017.
 */
public class TimeLoggingPlugin implements TaijituPlugin {
    private static final String NAME = "timeLog";
    private static final Logger logger = LoggerFactory.getLogger(TimeLoggingPlugin.class);
    private long comparisonStart;
    private long comparisonEnd;
    private long start;
    private long end;
    private int comparisonCount;

    @Override
    public String getName() {
        return NAME;
    }


    @Override
    public void preComparison(IPluginCfg pluginCfg, IComparisonCfg comparisonCfg) {
        comparisonStart = System.currentTimeMillis();
        logger.info("Comparison {} started.", comparisonCfg.getName());
    }

    @Override
    public void postComparison(IPluginCfg pluginCfg, IComparisonCfg comparisonCfg) {
        comparisonEnd = System.currentTimeMillis();
        logComparisonTimes(pluginCfg, comparisonCfg);
        comparisonCount++;
    }

    @Override
    public void start(IPluginCfg config) {
        start = System.currentTimeMillis();

    }

    @Override
    public void end(IPluginCfg config) {
        end = System.currentTimeMillis();
        logTotalTimes();

    }

    private void logComparisonTimes(IPluginCfg pluginConfig, IComparisonCfg comparisonCfg) {
        final PeriodFormatter formatter = ISOPeriodFormat.standard();
        final Period periodTotal = new Duration(comparisonStart, comparisonEnd).toPeriod();
        logger.info("Comparison {} completed in {}", comparisonCfg.getName(), formatter.print(periodTotal));
    }

    private void logTotalTimes() {
        final PeriodFormatter formatter = ISOPeriodFormat.standard();
        final Period periodTotal = new Duration(start, end).toPeriod();
        logger.info("Completed {} comparisons: {} ", comparisonCount, formatter.print(periodTotal));
    }

    public static IPluginCfg defaultConfig() {
        return () -> TimeLoggingPlugin.NAME;
    }
}
