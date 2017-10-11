package org.someth2say.taijitu.config;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.someth2say.taijitu.matcher.NamingColumnMatcher;
import org.someth2say.taijitu.plugins.logging.TimeLoggingPlugin;
import org.someth2say.taijitu.strategy.mapping.ParallelQueryingMappingStrategy;

public class DefaultConfig {
    public static final String DEFAULT_PLUGINS = TimeLoggingPlugin.NAME;
    public static final String DEFAULT_FILE_LOG_LEVEL = "OFF";
    public static final String DEFAULT_CONSOLE_LOG_LEVEL = "INFO";
    public static final String DEFAULT_OUTPUT_FOLDER = ".";
    public static final double DEFAULT_PRECISION_THRESHOLD = 0d;
    public static final int DEFAULT_FETCHSIZE = 1024;
    public static final int DEFAULT_THREADS = 1;
    public static final String DEFAULT_STRATEGY_NAME = ParallelQueryingMappingStrategy.NAME;
    public static final String DEFAULT_MATCHING_STRATEGY = NamingColumnMatcher.NAME;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyyMMdd");
    
    public static final String DEFAULT_LOG_FILE = "taijitu.logger";
    public static final String DEFAULT_CONFIG_FILE = "taijitu.properties";
    
}
