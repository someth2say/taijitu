package org.someth2say.taijitu.plugins.logging;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;
import org.someth2say.taijitu.TaijituException;
import org.someth2say.taijitu.plugins.TaijituPlugin;
import org.someth2say.taijitu.ComparisonRuntime;

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
    public void preComparison(ComparisonRuntime taijituData) throws TaijituException {
        comparisonStart = System.currentTimeMillis();
    }

    @Override
    public void postComparison(ComparisonRuntime taijituData) throws TaijituException {
        comparisonEnd = System.currentTimeMillis();
        logComparisonTimes(taijituData);
        comparisonCount++;
    }

    @Override
    public void start() throws TaijituException {
        start = System.currentTimeMillis();

    }

    @Override
    public void end() throws TaijituException {
        end = System.currentTimeMillis();
        logTotalTimes();
    }

    private void logComparisonTimes(ComparisonRuntime taijituData) {
        final PeriodFormatter formatter = ISOPeriodFormat.standard();
        final Period periodTotal = new Duration(comparisonStart, comparisonEnd).toPeriod();
        logger.info("DONE comparison " + taijituData.getTestName() + ":" + formatter.print(periodTotal));
    }

    private void logTotalTimes() {
        final PeriodFormatter formatter = ISOPeriodFormat.standard();
        final Period periodTotal = new Duration(start, end).toPeriod();
        logger.info("DONE " + comparisonCount + " comparisons:" + formatter.print(periodTotal));
    }

}
