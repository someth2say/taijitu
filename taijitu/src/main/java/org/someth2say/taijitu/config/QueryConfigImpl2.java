package org.someth2say.taijitu.config;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.someth2say.taijitu.config.ConfigurationLabels.Comparison;
import org.someth2say.taijitu.config.ConfigurationLabels.Comparison.Fields;
import org.someth2say.taijitu.config.ConfigurationLabels.Setup;

public class QueryConfigImpl2 implements QueryConfig {

	private final String name;
	private final ImmutableHierarchicalConfiguration configuration;
	private final ComparisonConfig comparisonConfig;

	public QueryConfigImpl2(final String name, final ImmutableHierarchicalConfiguration configuration,
			final ComparisonConfig comparisonConfig) {
		this.name = name;
		this.configuration = configuration;
		this.comparisonConfig = comparisonConfig;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getStatement() {
		String statement = configuration.getString(Comparison.QUERY);
		return statement != null ? statement : comparisonConfig.getStatement();
	}

	@Override
	public int getFetchSize() {
		Integer fs = configuration.getInteger(Setup.FETCH_SIZE, null);
		return fs != null ? fs : comparisonConfig.getFetchSize();
	}

	@Override
	public String[] getKeyFields() {
		String[] keys = configuration.get(String[].class, Fields.KEY, null);
		return keys != null ? keys : comparisonConfig.getKeyFields();
	}

	@Override
	public String getDatabase() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] getQueryParameters() {
		// TODO Auto-generated method stub
		return null;
	}

}
