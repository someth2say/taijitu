package org.someth2say.taijitu.util;

import org.apache.log4j.*;

import java.io.IOException;

/**
 * Created by Jordi Sola on 10/02/2017.
 */
public class LogUtils {

    public static final String DEFAULT_PATTERN = "%d{ABSOLUTE} %t %5p %c{1}:%M - %m%n";
    private static final Layout DEFAULT_LAYOUT = new PatternLayout(DEFAULT_PATTERN);
    private static final ConsoleAppender CONSOLE_APPENDER = new ConsoleAppender();

    private LogUtils() {
    }

    public static void addConsoleAppenderToRootLogger(Level level) {
        AddAppenderToLogger(level, Logger.getRootLogger(), new ConsoleAppender(DEFAULT_LAYOUT));
    }

    private static void AddAppenderToLogger(Level level, Logger rootLogger, AppenderSkeleton appender) {
        rootLogger.addAppender(appender);
        rootLogger.setLevel(level);
        appender.activateOptions();
    }

    public static void addFileAppenderToRootLogger(Level level, String logPattern, String fileName) throws IOException {
        AddAppenderToLogger(level, Logger.getRootLogger(), new FileAppender(DEFAULT_LAYOUT, fileName, true));
    }

    public static void addConsoleAppenderToTaijituLogger(Level level, String logPattern) {
        final Logger logger = Logger.getLogger("console");
        AddAppenderToLogger(level, logger, CONSOLE_APPENDER);
    }

    public static void addFileAppenderToTaijituLogger(Level level, String logPattern, String fileName) throws IOException {
        final Logger logger = Logger.getLogger("file");
        FileAppender appender = new FileAppender(DEFAULT_LAYOUT, fileName, true);
        AddAppenderToLogger(level, logger, appender);
    }


}
