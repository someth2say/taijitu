package org.someth2say.taijitu.config.delegate;

import org.someth2say.taijitu.util.Named;

import java.util.List;
import java.util.Properties;

public interface SourceConfigDelegate extends ConfigDelegate, Named {

    String getType();

    List<String> getKeyFields();

    Properties getSourceProperties();
}
