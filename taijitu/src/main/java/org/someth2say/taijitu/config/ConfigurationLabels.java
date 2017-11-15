package org.someth2say.taijitu.config;

/**
 * Created by Jordi Sola on 24/01/2017.
 */
//TODO: Deep cleanup
public final class ConfigurationLabels {
    private ConfigurationLabels() {




    }

    public static final class Sections {
        public static final String COMPARISON = "comparison";
        public static final String PLUGINS = "plugin";

        private Sections() {
        }
    }

    public static final class Comparison {
        public static final String STATEMENT = "sql";
        public static final String STRATEGY = "stream";
        public static final String QUERY_PARAMETERS = "queryParameters";
        public static final String EQUALITY_PARAMS = "parameters";
        public static final String FIELD_CLASS_STRICT = "exactClass";
        public static final String RESOURCE = "resouce";
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
		public static final String MAPPER_TYPE = "mapper";
    }

    public static final class Setup {
        public static final String FETCH_SIZE = "fetchSize";
        public static final String THREADS = "threads";
        public static final String SCAN_CLASSPATH = "scanClasspath";

        private Setup() {
        }

    }

}
