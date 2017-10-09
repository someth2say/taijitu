package org.someth2say.taijitu.query.queryactions;

import org.apache.log4j.Logger;
import org.someth2say.TestUtils;
import org.someth2say.taijitu.query.database.PropertiesBasedConnectionFactory;
import org.someth2say.taijitu.query.tuple.DefaultTuple;
import org.someth2say.taijitu.query.properties.HProperties;
import org.someth2say.taijitu.query.Query;
import org.someth2say.taijitu.query.QueryUtilsException;
import org.someth2say.taijitu.query.tuple.ITupleFactory;
import org.someth2say.taijitu.query.querywalker.QueryWalker;
import org.someth2say.taijitu.commons.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * @author Jordi Sola
 */
public final class QueryDumper {
	private static final int DEFAULT_FECH_SIZE = 128;

	private static final Logger log = Logger.getLogger(QueryDumper.class);

	private QueryDumper() {
	}

	public static void main(String[] args) {
		if (args.length != 2) {
			FileUtil.dumpResource("usage-queryDumper.txt");
		} else {
			try {
				dumpToFile(new File(args[1]), new HProperties(args[0]));
			} catch (QueryUtilsException e) {
				System.out.println("Unable to dump results: " + e.getMessage());
			}
		}

	}

	public static void dumpToFile(File file, HProperties properties) throws QueryUtilsException {
		createDumpFile(file);
		try (final FileOutputStream fileWriter = new FileOutputStream(file)) {
			dump(properties, fileWriter);
		} catch (IOException e) {
			throw new QueryUtilsException("Unable to write to destination file " + file.getAbsolutePath(), e);
		}
	}

	private static void createDumpFile(File file) throws QueryUtilsException {
		if (!file.exists()) {
			try {
				log.debug("Creating new dump file: " + file.getAbsolutePath());
				if (!file.createNewFile()) {
					throw new QueryUtilsException("Unable to create output file " + file.getAbsolutePath());
				}
			} catch (IOException e) {
				throw new QueryUtilsException("Exception creating output file " + file.getAbsolutePath(), e);
			}
		}
	}

	public static void dump(HProperties properties, OutputStream os) throws QueryUtilsException {
		final String connectionName = properties.getProperty("querydumper.connection");

		final PropertiesBasedConnectionFactory connectionFactory = new PropertiesBasedConnectionFactory(properties,
				TestUtils.PROPERTIES_ROOT);
		String sql = properties.getProperty("querydumper.sql");
		log.debug("Query: " + sql);
		QueryActions<DefaultTuple> queryActions = new CVSStreamQueryActions(os);
		log.debug("Starting dump.");
		final ITupleFactory<DefaultTuple> factory = DefaultTuple.Factory.INSTANCE;
		final String queryName = "querydumper";
		Query query = new Query(queryName, sql, connectionFactory, connectionName, null, new ArrayList<>());
		query.setFetchSize(DEFAULT_FECH_SIZE);

		QueryWalker.walkValues(query, factory, queryActions);
		log.debug("Dump finished.");

		connectionFactory.closeAll();

	}

	private static class CVSStreamQueryActions implements QueryActions<DefaultTuple> {
		private final OutputStream os;
		boolean initialized;
		private String[] columnDescriptions;

		public CVSStreamQueryActions(OutputStream _os) {
			this.os = _os;
			initialized = false;
		}

		@Override
		public void start(String[] columnDescriptions) throws QueryActionsException {
			// DO Nothing
			this.columnDescriptions = columnDescriptions;
		}

		@Override
		public void step(DefaultTuple currentRecord) throws QueryActionsException {
			try {
				if (!this.initialized) {
					writeColumnsHeader();
					this.initialized = true;
				}
				writeColumnsValues(currentRecord);
			} catch (IOException e) {
				throw new QueryActionsException("Can't write to output stream.", e);
			}
		}

		private void writeColumnsValues(DefaultTuple currentRecord) throws IOException {
			int pos = 0;
			while (pos < currentRecord.size()) {
				Object value = currentRecord.getValue(pos++);
				os.write((value == null ? "\t" : value.toString() + "\t").getBytes());
			}

			os.write("\n".getBytes());
		}

		private void writeColumnsHeader() throws IOException {
			for (String column : columnDescriptions) {
				os.write((column + '\t').getBytes());
			}
			os.write("\n".getBytes());
		}

		@Override
		public void end() throws QueryActionsException {
			// DO Nothing
		}
	}
}
