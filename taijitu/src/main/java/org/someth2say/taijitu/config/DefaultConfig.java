package org.someth2say.taijitu.config;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.someth2say.taijitu.compare.equality.ToStringEqualityStrategy;
import org.someth2say.taijitu.config.interfaces.IEqualityCfg;
import org.someth2say.taijitu.config.interfaces.IPluginCfg;
import org.someth2say.taijitu.config.interfaces.IStrategyCfg;
import org.someth2say.taijitu.matcher.NamingFieldMatcher;
import org.someth2say.taijitu.plugins.logging.TimeLoggingPlugin;
import org.someth2say.taijitu.strategy.mapping.MappingStrategy;

import java.util.List;

public class DefaultConfig {

    public static final char DEFAULT_LIST_DELIMITER = ',';

    public static final String DEFAULT_FILE_LOG_LEVEL = "OFF";
    public static final String DEFAULT_CONSOLE_LOG_LEVEL = "INFO";
    public static final String DEFAULT_OUTPUT_FOLDER = ".";
    public static final int DEFAULT_FETCHSIZE = 1024;
    public static final int DEFAULT_THREADS = 1;
    public static final boolean DEFAULT_SCAN_CLASSPATH = false;

    public static final IStrategyCfg DEFAULT_STRATEGY_CONFIG = MappingStrategy.defaultConfig();
    public static final IEqualityCfg DEFAULT_EQUALITY_CONFIG = ToStringEqualityStrategy.defaultConfig();
    public static final List<IPluginCfg> DEFAULT_PLUGINS_CONFIG = List.of(TimeLoggingPlugin.defaultConfig());

    public static final String DEFAULT_MATCHING_STRATEGY_NAME = NamingFieldMatcher.NAME;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyyMMdd");
    public static final Object[] DEFAULT_QUERY_PARAMETERS = new Object[0];

    public static final String DEFAULT_LOG_FILE = "taijitu.logger";
    public static final String DEFAULT_CONFIG_FILE = "taijitu.properties";

    public static final boolean DEFAULT_FIELD_CLASS_STRICT = false;
}
