package org.someth2say.taijitu.config.delegate;

import java.util.Properties;

public interface DatabaseConfigDelegate extends ConfigDelegate{
    Properties getDatabaseProperties();
}
