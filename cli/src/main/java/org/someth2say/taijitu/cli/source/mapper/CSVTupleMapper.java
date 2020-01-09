package org.someth2say.taijitu.cli.source.mapper;

import org.someth2say.taijitu.cli.source.Source;

import java.util.stream.Stream;

public class CSVTupleMapper extends AbstractSourceMapper<String[], Object[]> {

    public CSVTupleMapper() {
        super();
    }

    @Override
    public Source<Object[]> apply(Source<String[]> source) {
        return new AbstractMappedTupleSource(source) {

            Object[] mapItem(String[] csvEntry) {
                return csvEntry;
            }

            @Override
            public Stream<Object[]> stream() {
                return source.stream().map(this::mapItem);
            }
        };
    }
}
