package org.someth2say.taijitu.commons.database;

/**
 * Created by Jordi Sola on 10/02/2017.
 */
@Deprecated
// Replaced by HicariCP config.
public interface ConnectionDataFactory {
    String getConnectionKey();

    String getDriver();

    String getConnectionString();

    String getUsername();

    String getPassword();
}
