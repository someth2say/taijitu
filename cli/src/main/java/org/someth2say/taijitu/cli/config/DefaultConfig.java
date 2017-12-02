package org.someth2say.taijitu.cli.config;

import org.someth2say.taijitu.cli.config.interfaces.DefaultEqualityConfig;
import org.someth2say.taijitu.cli.config.interfaces.IEqualityCfg;
import org.someth2say.taijitu.cli.config.interfaces.IPluginCfg;
import org.someth2say.taijitu.cli.plugins.logging.TimeLoggingPlugin;
import org.someth2say.taijitu.compare.equality.value.JavaObject;
import org.someth2say.taijitu.compare.equality.value.ObjectToString;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DefaultConfig {

    public static final char DEFAULT_LIST_DELIMITER = ',';

    public static final int DEFAULT_FETCHSIZE = 1024;
    public static final int DEFAULT_THREADS = 1;
    public static final boolean DEFAULT_SCAN_CLASSPATH = false;

    //TODO: We are here adding two equalities: a simple one, and a ComparableCategorizer one. Maybe this should be split
    public static final List<IEqualityCfg> DEFAULT_EQUALITY_CONFIG = Arrays.asList((DefaultEqualityConfig) JavaObject.class::getSimpleName, (DefaultEqualityConfig) ObjectToString.class::getSimpleName);
    public static final List<IPluginCfg> DEFAULT_PLUGINS_CONFIG = Collections.singletonList(TimeLoggingPlugin.defaultConfig());

    public static final String DEFAULT_CONFIG_FILE = "taijitu.properties";

    public static final boolean DEFAULT_FIELD_CLASS_STRICT = false;
    public static final List<String> DEFAULT_KEY_FIELDS = Collections.emptyList();
    public static final List<String> DEFAULT_SORT_FIELDS = Collections.emptyList();
    public static final List<String> DEFAULT_COMPARE_FIELDS = Collections.emptyList();
}
