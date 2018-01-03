package org.someth2say.taijitu.cli.source.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.cli.source.FieldDescription;
import org.someth2say.taijitu.cli.source.Source;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResultSetTupleMapper extends AbstractSourceMapper<ResultSet, Object[]> {
    private static final Logger logger = LoggerFactory.getLogger(ResultSetTupleMapper.class);

    @Override
    public Source<Object[]> apply(Source<ResultSet> source) {
        return new AbstractMappedTupleSource(source) {

            Object[] mapItem(ResultSet rs) {
                return getProvidedFields().stream().map(fd -> {
                    try {
                        return rs.getObject(fd.getName());
                    } catch (SQLException e) {
                        logger.error("Can\'t retrieve value for field " + fd, e);
                        return null;
                    }
                }).collect(Collectors.toList()).toArray(new Object[0]);
            }

            @Override
            public Stream<Object[]> stream() {
                return source.stream().map(this::mapItem);
            }

            @Override
            public void close() throws ClosingException {
                source.close();
            }

            @Override
            public List<FieldDescription<?>> getProvidedFields() {
                return source.getProvidedFields();
            }
        };
    }

    @Override
    public Class<Object[]> getTypeParameter() {
        return Object[].class;
    }
}
