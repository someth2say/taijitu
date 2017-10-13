package org.someth2say.taijitu.query.database;

import org.someth2say.taijitu.query.QueryUtilsException;

import java.sql.Connection;

/**
 * Created by Jordi Sola on 23/02/2017.
 */
@Deprecated
public interface IConnectionFactory {
    Connection getConnection(final String name) throws QueryUtilsException;

    void closeAll() throws QueryUtilsException;
}
