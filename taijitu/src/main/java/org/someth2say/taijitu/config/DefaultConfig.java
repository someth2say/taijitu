package org.someth2say.taijitu.config;

import org.someth2say.taijitu.compare.equality.stream.mapping.MappingStreamEquality;
import org.someth2say.taijitu.compare.equality.value.ToStringValueEquality;
import org.someth2say.taijitu.config.interfaces.IEqualityCfg;
import org.someth2say.taijitu.config.interfaces.IPluginCfg;
import org.someth2say.taijitu.config.interfaces.IStrategyCfg;
import org.someth2say.taijitu.plugins.logging.TimeLoggingPlugin;

import java.util.Collections;
import java.util.List;

public class DefaultConfig {

    public static final char DEFAULT_LIST_DELIMITER = ',';

    public static final int DEFAULT_FETCHSIZE = 1024;
    public static final int DEFAULT_THREADS = 1;
    public static final boolean DEFAULT_SCAN_CLASSPATH = false;

    public static final IStrategyCfg DEFAULT_STRATEGY_CONFIG = MappingStreamEquality.defaultConfig();
    public static final List<IEqualityCfg> DEFAULT_EQUALITY_CONFIG = Collections.singletonList(ToStringValueEquality.defaultConfig());
    public static final List<IPluginCfg> DEFAULT_PLUGINS_CONFIG = Collections.singletonList(TimeLoggingPlugin.defaultConfig());

    public static final String DEFAULT_CONFIG_FILE = "taijitu.properties";

    public static final boolean DEFAULT_FIELD_CLASS_STRICT = false;
    public static final List<String> DEFAULT_KEY_FIELDS = Collections.singletonList("*");
}
