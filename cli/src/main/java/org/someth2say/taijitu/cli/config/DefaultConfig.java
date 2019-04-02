package org.someth2say.taijitu.cli.config;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.someth2say.taijitu.cli.config.interfaces.IEqualityCfg;
import org.someth2say.taijitu.equality.impl.value.ObjectHasher;
import org.someth2say.taijitu.equality.impl.value.ObjectToStringComparatorHasher;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class DefaultConfig {

    public static final char DEFAULT_LIST_DELIMITER = ',';

    public static final int DEFAULT_FETCH_SIZE = 1024;
    public static final int DEFAULT_THREADS = 1;
    public static final boolean DEFAULT_SCAN_CLASSPATH = false;

    //TODO: We are here adding two equalities: a simple one, and a HasherComparer one. Maybe this should be split
    public static final List<IEqualityCfg> DEFAULT_EQUALITY_CONFIGS = Arrays.asList(
            new IEqualityCfg() {
                @Override
                public HierarchicalConfiguration<?> getConfiguration() {
                    return null;
                }

                @Override
                public String getName() {
                    return ObjectHasher.class.getSimpleName();
                }
            },
            new IEqualityCfg() {
                @Override
                public HierarchicalConfiguration<?> getConfiguration() {
                    return null;
                }

                @Override
                public String getName() {
                    return ObjectToStringComparatorHasher.class.getSimpleName();
                }
            }
    );

    public static final String DEFAULT_CONFIG_FILE = "taijitu.properties";

    public static final boolean DEFAULT_FIELD_CLASS_STRICT = false;
    public static final List<String> DEFAULT_KEY_FIELDS = Collections.emptyList();
    public static final List<String> DEFAULT_SORT_FIELDS = Collections.emptyList();
    public static final List<String> DEFAULT_COMPARE_FIELDS = Collections.emptyList();

    public static final Properties DEFAULT_EMPTY_PROPERTIES = new Properties();
}
