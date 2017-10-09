package org.someth2say.taijitu.query;

import org.junit.After;
import org.junit.Test;
import org.someth2say.TestUtils;
import org.someth2say.taijitu.query.database.ConnectionManager;
import org.someth2say.taijitu.query.database.IConnectionFactory;
import org.someth2say.taijitu.query.database.PropertiesBasedConnectionFactory;
import org.someth2say.taijitu.query.database.PropertyBasedConnectionDataFactory;
import org.someth2say.taijitu.query.tuple.DefaultTuple;
import org.someth2say.taijitu.query.properties.HProperties;
import org.someth2say.taijitu.query.tuple.ITupleFactory;
import org.someth2say.taijitu.query.queryactions.OutputStreamQueryActions;
import org.someth2say.taijitu.query.querywalker.QueryWalker;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by Jordi Sola on 10/02/2017.
 */
public class QueryWalkerTest {

    private final static String DB_NAME = "test";
    private final static String DB_USER = "user";
    private final static String DB_PWD = "pwd";

    @After
    public void dropTables() throws SQLException, QueryUtilsException {

        final HProperties databaseProps = TestUtils.makeH2DatabaseProps(DB_NAME, DB_USER, DB_PWD);
        // Create the tables and test data
        try (Connection conn = ConnectionManager.getConnection(new PropertyBasedConnectionDataFactory(databaseProps, DB_NAME, TestUtils.PROPERTIES_ROOT, DB_NAME))) {
            TestUtils.dropRegisteredTables(conn);
        }
    }

    @Test(expected = QueryUtilsException.class)
    public void testException() throws QueryUtilsException {

        String name = "test";
        String str = "select * from test";
        List<Object> paramValues = Collections.emptyList();
        Query query = new Query(name, str, null, "", null, paramValues);

        ITupleFactory<DefaultTuple> factory = new DefaultTuple.Factory();
        QueryWalker.getMemStoreValues(query, factory);
        fail("Exception should be thrown!");
    }

    @Test
    public void walkTest() throws QueryUtilsException, SQLException, IOException {
        String testName = "test";
        String str = "select * from test";

        final HProperties databaseProps = TestUtils.makeH2DatabaseProps(DB_NAME, DB_USER, DB_PWD);
        IConnectionFactory connectionFactory = new PropertiesBasedConnectionFactory(databaseProps, TestUtils.PROPERTIES_ROOT);
        try (Connection conn = ConnectionManager.getConnection(new PropertyBasedConnectionDataFactory(databaseProps, DB_NAME, TestUtils.PROPERTIES_ROOT, DB_NAME))) {

            String tableName = "test";
            String[] columnDefs = {"key VARCHAR(255) PRIMARY KEY", "value VARCHAR(1023)"};
            String[][] columnValues = {{"1", "'UNO'"}, {"2", "'DOS'"}};
            TestUtils.createTable(conn, tableName, columnDefs, columnValues);

            Query query = new Query(testName, str, connectionFactory, DB_NAME, new String[]{"KEY", "VALUE"}, Collections.emptyList());

            ITupleFactory<DefaultTuple> factory = new DefaultTuple.Factory();
            try (PipedInputStream inputStream = new PipedInputStream(); OutputStream os = new PipedOutputStream(inputStream)) {
                OutputStreamQueryActions<DefaultTuple> walker = new OutputStreamQueryActions<>(os);
                QueryWalker.walkValues(query, factory, walker);
                String output = dumpInputStreamToString(inputStream);
                assertTrue("Output should contain table columns", output.contains("1,UNO"));
                assertTrue("Output should contain table columns", output.contains("2,DOS"));
            }
        }
        connectionFactory.closeAll();
    }

    private String dumpInputStreamToString(PipedInputStream inputStream) throws IOException {
        byte[] buffer = new byte[inputStream.available()];
        inputStream.read(buffer);
        return new String(buffer);
    }
}