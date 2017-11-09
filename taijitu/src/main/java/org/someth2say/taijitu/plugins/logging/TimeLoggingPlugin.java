package org.someth2say.taijitu.plugins.logging;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;
import org.someth2say.taijitu.TaijituException;
import org.someth2say.taijitu.config.interfaces.IPluginCfg;
import org.someth2say.taijitu.plugins.TaijituPlugin;
import org.someth2say.taijitu.ComparisonContext;

/**
 * Created by Jordi Sola on 24/02/2017.
 */
public class TimeLoggingPlugin implements TaijituPlugin {
    public static final String NAME = "timeLog";
    private static final Logger logger = Logger.getLogger(TimeLoggingPlugin.class);
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
    public void preComparison(ComparisonContext taijituData, IPluginCfg comparisonConfig) throws TaijituException {
        comparisonStart = System.currentTimeMillis();
    }

    @Override
    public void postComparison(ComparisonContext taijituData, IPluginCfg comparisonConfig) throws TaijituException {
        comparisonEnd = System.currentTimeMillis();
        logComparisonTimes();
        comparisonCount++;
    }

    @Override
    public void start(IPluginCfg config) throws TaijituException {
        start = System.currentTimeMillis();

    }

    @Override
    public void end(IPluginCfg config) throws TaijituException {
        end = System.currentTimeMillis();
        logTotalTimes();

    }

    private void logComparisonTimes() {
        final PeriodFormatter formatter = ISOPeriodFormat.standard();
        final Period periodTotal = new Duration(comparisonStart, comparisonEnd).toPeriod();
        logger.info("DONE comparison:" + formatter.print(periodTotal));
    }

    private void logTotalTimes() {
        final PeriodFormatter formatter = ISOPeriodFormat.standard();
        final Period periodTotal = new Duration(start, end).toPeriod();
        logger.info("DONE " + comparisonCount + " comparisons:" + formatter.print(periodTotal));
    }

    public static IPluginCfg defaultConfig() {
        return () -> TimeLoggingPlugin.NAME;
    }
}
