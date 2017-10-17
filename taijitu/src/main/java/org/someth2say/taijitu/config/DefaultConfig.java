package org.someth2say.taijitu.config;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.someth2say.taijitu.matcher.NamingColumnMatcher;
import org.someth2say.taijitu.plugins.logging.TimeLoggingPlugin;
import org.someth2say.taijitu.strategy.sorted.SortedStrategy;

public class DefaultConfig {
    public static final String DEFAULT_PLUGINS = TimeLoggingPlugin.NAME;
    public static final String DEFAULT_FILE_LOG_LEVEL = "OFF";
    public static final String DEFAULT_CONSOLE_LOG_LEVEL = "INFO";
    public static final String DEFAULT_OUTPUT_FOLDER = ".";
    public static final double DEFAULT_PRECISION_THRESHOLD = 0d;
    public static final int DEFAULT_FETCHSIZE = 1024;
    public static final int DEFAULT_THREADS = 1;
    public static final boolean DEFAULT_SCAN_CLASSPATH = false;

    //TODO: Fix default strategy!!!!
    public static final String DEFAULT_STRATEGY_NAME = SortedStrategy.NAME;
    public static final StrategyConfig DEFAULT_STRATEGY_CONFIG = SortedStrategy.defaultConfig();

    public static final String DEFAULT_COLUMN_MATCHING_STRATEGY_NAME = NamingColumnMatcher.NAME;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyyMMdd");
    public static final Object[] DEFAULT_QUERY_PARAMETERS = new Object[0];
    
    public static final String DEFAULT_LOG_FILE = "taijitu.logger";
    public static final String DEFAULT_CONFIG_FILE = "taijitu.properties";
    
}
