package org.someth2say.taijitu.query.querywalker;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.someth2say.taijitu.query.Query;
import org.someth2say.taijitu.query.QueryUtilsException;
import org.someth2say.taijitu.query.discarter.Discarter;
import org.someth2say.taijitu.query.discarter.TimeBasedLog4jDiscarter;
import org.someth2say.taijitu.query.objects.ObjectArray;
import org.someth2say.taijitu.query.objects.IObjectsFactory;
import org.someth2say.taijitu.query.queryactions.MemStoreQueryActions;
import org.someth2say.taijitu.query.queryactions.QueryActions;
import org.someth2say.taijitu.query.queryactions.QueryActionsException;

import java.sql.*;
import java.util.Arrays;

/**
 * @author Jordi Sola
 */
public final class QueryWalker {
	private static final Logger logger = Logger.getLogger(QueryWalker.class);

	private QueryWalker() {
	}

	public static <T extends ObjectArray> MemStoreResults<T> getMemStoreValues(Query query,
			IObjectsFactory<T> factory) throws QueryUtilsException {
		MemStoreQueryActions<T> queryWalker = new MemStoreQueryActions<>();
		walkValues(query, factory, queryWalker);
		return new MemStoreResults<>(queryWalker.getColumnDescriptions(), queryWalker.getValues());
	}

	public static <T extends ObjectArray> void getMemStoreValuesInto(Query query, IObjectsFactory<T> factory,
			MemStoreResults<T> result) throws QueryUtilsException {
		MemStoreQueryActions<T> queryWalker = new MemStoreQueryActions<>(result);
		walkValues(query, factory, queryWalker);
		result.setDescriptions(queryWalker.getColumnDescriptions());
		result.setValues(queryWalker.getValues());
	}

	public static <T extends ObjectArray> void walkValues(Query query, IObjectsFactory<T> factory,
			QueryActions<T> queryActions) throws QueryUtilsException {
		// Assuming connection is already open! So we should not close it
		final Connection connection = query.getConnection();
		if (connection == null) {
			// Use connection factory

			throw new QueryUtilsException("Connection should not be null.");
		}
		final String queryStr = query.getQueryString();
		final int fetchSize = query.getFetchSize();

		if (query.getParameterValues() != null && !query.getParameterValues().isEmpty()) {
			walkValuesWithParameters(query, factory, queryActions, connection, queryStr, fetchSize);
		} else {
			walkValuesWithoutParameters(query, factory, queryActions, connection, queryStr, fetchSize);
		}
	}

	private static <T extends ObjectArray> void walkValuesWithoutParameters(Query query, IObjectsFactory<T> factory,
			QueryActions<T> queryActions, Connection connection, String queryStr, int fetchSize)
			throws QueryUtilsException {
		try (final Statement statement = getStatement(connection, fetchSize);
				final ResultSet resultSet = getResultSet(queryStr, statement)) {
			walkResultSet(query, factory, queryActions, resultSet);
		} catch (SQLException e) {
			throw new QueryUtilsException("Problems while executing parametrised query:" + e.getMessage(), e);
		}
	}

	private static <T extends ObjectArray> void walkValuesWithParameters(Query query, IObjectsFactory<T> factory,
			QueryActions<T> queryActions, Connection connection, String queryStr, int fetchSize)
			throws QueryUtilsException {
		try (final PreparedStatement preparedStatement = getPreparedStatement(query, connection, queryStr, fetchSize);
				final ResultSet resultSet = getResultSet(preparedStatement)) {
			walkResultSet(query, factory, queryActions, resultSet);
		} catch (SQLException e) {
			throw new QueryUtilsException("Problems while executing parameter-less query:" + e.getMessage(), e);
		}
	}

	private static ResultSet getResultSet(String queryStr, Statement statement) throws QueryUtilsException {
		ResultSet resultSet;
		try {
			resultSet = statement.executeQuery(queryStr);
		} catch (SQLException e) {
			throw new QueryUtilsException("Can\'t execute statement.", e);
		}
		return resultSet;
	}

	private static Statement getStatement(Connection connection, int fetchSize) throws QueryUtilsException {
		Statement statement;
		try {
			statement = connection.createStatement();
			statement.setFetchSize(fetchSize);
		} catch (SQLException var2) {
			throw new QueryUtilsException("Can\'t prepare statement.", var2);
		}
		return statement;
	}

	private static ResultSet getResultSet(PreparedStatement preparedStatement) throws QueryUtilsException {
		ResultSet resultSet;
		try {
			resultSet = preparedStatement.executeQuery();
		} catch (SQLException e) {
			throw new QueryUtilsException("Can\'t execute query.", e);
		}
		return resultSet;
	}

	private static PreparedStatement getPreparedStatement(Query query, Connection connection, String queryStr,
			int fetchSize) throws QueryUtilsException {
		PreparedStatement preparedStatement;
		try {
			preparedStatement = connection.prepareStatement(queryStr);
			preparedStatement.setFetchSize(fetchSize);
			int paramIdx = 1;
			for (Object object : query.getParameterValues()) {
				if (object instanceof java.util.Date) {
					preparedStatement.setDate(paramIdx, new Date(((java.util.Date) object).getTime()));
				} else {
					preparedStatement.setObject(paramIdx, object);
				}
				paramIdx++;
			}
		} catch (SQLException e) {
			throw new QueryUtilsException("Can\'t prepare statement.", e);
		}
		return preparedStatement;
	}

	private static <T extends ObjectArray> void walkResultSet(Query query, IObjectsFactory<T> factory,
			QueryActions<T> queryActions, ResultSet rs) throws QueryUtilsException {
		Discarter discarter = TimeBasedLog4jDiscarter.newInstance(1000, logger, Level.INFO);
		try {
			int recordCount = 0;
			String[] columnDescriptions = getColumnsDescription(query, rs);
			queryActions.start(columnDescriptions);

			while (rs.next()) {
				++recordCount;
				final T values = factory.fromRecordSet(columnDescriptions, rs);
				queryActions.step(values);
				discarter.execute("Processed ", recordCount, " records so far...");
			}
			queryActions.end();
			logger.debug("Query " + query.getQueryName() + " finished. Processed " + String.format("%,d", recordCount)
					+ " records.");
		} catch (QueryUtilsException | SQLException e) {
			throw new QueryUtilsException("Problem getting data for " + query.getQueryName() + ":" + e.getMessage(), e);
		} catch (QueryActionsException e) {
			throw new QueryUtilsException(
					"Problem while walking data for  " + query.getQueryName() + ":" + e.getMessage(), e);
		}
	}

	private static String[] getColumnsDescription(Query query, ResultSet rs) throws QueryUtilsException {
		ResultSetMetaData resultSetMetaData;
		try {
			resultSetMetaData = rs.getMetaData();
		} catch (SQLException e) {
			throw new QueryUtilsException("Can\'t get metadata from recordset.", e);
		}

		String[] columnDescriptions;
		String[] columnNames = query.getColumnNames();
		if (columnNames == null || columnNames.length == 1 && "*".equals(columnNames[0])) {
			columnDescriptions = getColumnsFromMeta(query, resultSetMetaData);
			query.setColumnNames(columnDescriptions);
		} else {
			columnDescriptions = columnNames;
		}

		return columnDescriptions;
	}

	private static String[] getColumnsFromMeta(Query query, ResultSetMetaData resultSetMetaData)
			throws QueryUtilsException {
		int rsColumnCount = getRsColumnCount(resultSetMetaData);
		String[] result = new String[rsColumnCount];
		for (int columnIdx = 1; columnIdx <= rsColumnCount; ++columnIdx) {
			String columnName = getColumnName(resultSetMetaData, columnIdx);
			if (columnName != null && !"".equals(columnName)) {
				result[columnIdx - 1] = columnName;
			}
		}
		logger.debug(
				"Query " + query.getQueryName() + " provided following fields from meta:" + Arrays.toString(result));
		return result;
	}

	private static String getColumnName(ResultSetMetaData resultSetMetaData, int columnIdx) throws QueryUtilsException {
		String columnName;
		try {
			columnName = resultSetMetaData.getColumnName(columnIdx);
		} catch (SQLException e) {
			throw new QueryUtilsException(
					"Unable to obtain column name for query (maybe not supported by JDBC driver?)", e);
		}
		return columnName;
	}

	private static int getRsColumnCount(ResultSetMetaData resultSetMetaData) throws QueryUtilsException {
		int rsColumnCount;
		try {
			rsColumnCount = resultSetMetaData.getColumnCount();
		} catch (SQLException e) {
			throw new QueryUtilsException(
					"Unable to obtain column metadata for query (maybe not supported by JDBC driver?)", e);
		}
		return rsColumnCount;
	}
}
