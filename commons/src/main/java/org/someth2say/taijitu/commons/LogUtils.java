package org.someth2say.taijitu.commons;

import org.apache.log4j.*;

/**
 * Created by Jordi Sola on 10/02/2017.
 */
public class LogUtils {

    public static final String DEFAULT_PATTERN = "%d{ABSOLUTE} %t %5p %c{1}:%M - %m%n";
    static final ConsoleAppender CONSOLE_APPENDER = new ConsoleAppender();
    static final FileAppender FILE_APPENDER = new FileAppender();

    private LogUtils() {
    }

    public static void addConsoleAppenderToRootLogger(Level level, String logPattern) {
        final Logger logger = Logger.getRootLogger();
        final Layout layout = new PatternLayout(logPattern);

        CONSOLE_APPENDER.setLayout(layout);
        logger.addAppender(CONSOLE_APPENDER);
        logger.setLevel(level);
        CONSOLE_APPENDER.activateOptions();
    }

    public static void addFileAppenderToRootLogger(Level level, String logPattern, String fileName) {
        final Logger logger = Logger.getRootLogger();
        final Layout layout = new PatternLayout(logPattern);

        FILE_APPENDER.setLayout(layout);
        FILE_APPENDER.setFile(fileName);
        FILE_APPENDER.setAppend(true);
        logger.addAppender(FILE_APPENDER);
        logger.setLevel(level);
        FILE_APPENDER.activateOptions();
    }
}
