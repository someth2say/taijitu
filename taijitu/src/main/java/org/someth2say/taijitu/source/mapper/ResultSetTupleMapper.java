package org.someth2say.taijitu.source.mapper;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.source.Source;
import org.someth2say.taijitu.tuple.FieldDescription;
import org.someth2say.taijitu.tuple.Tuple;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResultSetTupleMapper extends AbstractSourceMapper<ResultSet, Tuple>{
    private static final Logger logger = Logger.getLogger(ResultSetTupleMapper.class);
    public static final String NAME = "resultSetToTuple";

	@Override
	public String getName() {
		return ResultSetTupleMapper.NAME;
	}

    @Override
    public Source<Tuple> apply(Source<ResultSet> other) {
        return new Source<Tuple>() {
            @Override
            public List<FieldDescription> getProvidedFields() {
                return other.getProvidedFields();
            }

            @Override
            public Stream<Tuple> stream() {
                return other.stream().map(rs -> new Tuple(getProvidedFields().stream().map(fd -> {
                    try {
                        return rs.getObject(fd.getName());
                    } catch (SQLException e) {
                        logger.error("Can\'t retrieve value for field " + fd, e);
                        return null;
                    }
                }).collect(Collectors.toList()).toArray(new Object[0])));
            }

            @Override
            public void close() throws ClosingException {
                other.close();
            }

            @Override
            public String getName() {
                return other.getName();
            }
        };
    }
}
