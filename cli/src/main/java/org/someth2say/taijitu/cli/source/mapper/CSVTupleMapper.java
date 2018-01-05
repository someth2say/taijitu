package org.someth2say.taijitu.cli.source.mapper;

import org.someth2say.taijitu.cli.source.FieldDescription;
import org.someth2say.taijitu.cli.source.Source;

import java.util.List;
import java.util.stream.Stream;

//TODO: Maybe this is the point to add some semantics to CSV: Parse provided fields names, try to cast values to the right object
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
