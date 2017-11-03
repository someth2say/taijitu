package org.someth2say.taijitu.config;

/**
 * Created by Jordi Sola on 24/01/2017.
 */
//TODO: Deep cleanup
public final class ConfigurationLabels {
    public static final String DATE_PARAMETER_KEYWORD = "Date";
    public static final String DATABASE_SECTION = "database";

    private ConfigurationLabels() {
    }

    public static final class Sections {
        public static final String SETUP = "setup";
        public static final String COMPARISON = "comparison";
        public static final String DATABASE = "database";
        public static final String PLUGINS = "plugin";

        private Sections() {
        }
    }

    public static final class Comparison {
        public static final String STATEMENT = "sql";
        @Deprecated
        public static final String SOURCE = "source";
        @Deprecated
        public static final String TARGET = "target";
        public static final String STRATEGY = "strategy";
        public static final String QUERY_PARAMETERS = "queryParameters";
        public static final String EQUALITY_PARAMS = "parameters";
        public static final String FIELD_CLASS_STRICT = "exactClass";
        public static final String RESOUCE = "resouce";
        public static final String SOURCE_TYPE = "type";
        public static final String SOURCES = "sources";
        public static final String SOURCE_FETCH_PROPERTIES = "fetchProperties";
        public static final String SOURCE_BUILD_PROPERTIES = "buildProperties";

        private Comparison() {
        }

        public static final class Fields {
            public static final String KEYS = "keys";

            private Fields() {
            }
        }

        public static final String FIELD_NAME = "field";
        public static final String FIELD_CLASS = "class";

        public static final String EQUALITY ="equality";
    }

    public static final class Setup {
        public static final String FETCH_SIZE = "fetchSize";
        public static final String CONSOLE_LOG = "consoleLog";
        public static final String THREADS = "threads";
        public static final String FILE_LOG = "fileLog";
        public static final String OUTPUT_FOLDER = "outputFolder";
        public static final String MATCHING_STRATEGY = "matching";
        public static final String SCAN_CLASSPATH = "scanClasspath";

        private Setup() {
        }

    }

}
