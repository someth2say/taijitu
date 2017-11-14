package org.someth2say.taijitu.source.mapper;

import org.someth2say.taijitu.source.Source;
import org.someth2say.taijitu.source.FieldDescription;
import org.someth2say.taijitu.tuple.Tuple;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class CSVTupleMapper extends AbstractSourceMapper<Object[], Tuple> {

    public static final String NAME = "csvToTuple";

    public CSVTupleMapper() {
        super();
    }

    @Override
    public String getName() {
        return CSVTupleMapper.NAME;
    }


    @Override
    public Class<Tuple> getTypeParameter() {
        return Tuple.class;
    }

    @Override
    public Source<Tuple> apply(Source<Object[]> source) {
        return new Source<Tuple>() {

            Tuple mapItem(Object[] csvEntry) {
                return new Tuple(csvEntry);
            }

            @Override
            public List<FieldDescription<?>> getProvidedFields() {
                // Same names, types and positions
                return source.getProvidedFields();
            }

            @Override
            public <V> Function<Tuple, V> getExtractor(FieldDescription<V> fd) {
                int index = getProvidedFields().indexOf(fd);
                if (index < 0) return null;
                //TODO: Add classes to tuple elements, so cast is unneded.
                return (Tuple tuple) -> (V) tuple.getValue(index);
            }

            @Override
            public Stream<Tuple> stream() {
                return source.stream().map(this::mapItem);
            }

            @Override
            public void close() throws ClosingException {
                source.close();
            }

            @Override
            public Class<Tuple> getTypeParameter() {
                return Tuple.class;
            }

            @Override
            public String getName() {
                return source.getName();
            }
        };
    }


}
