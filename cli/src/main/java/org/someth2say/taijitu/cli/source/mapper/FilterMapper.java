package org.someth2say.taijitu.cli.source.mapper;

import org.someth2say.taijitu.cli.source.AbstractSource;
import org.someth2say.taijitu.cli.source.FieldDescription;
import org.someth2say.taijitu.cli.source.Source;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class FilterMapper<ORIG_TYPE> extends AbstractSourceMapper<ORIG_TYPE, ORIG_TYPE> {


    private final Predicate<ORIG_TYPE> predicate;

    public FilterMapper(Predicate<ORIG_TYPE> predicate) {
        super();
        this.predicate = predicate;
    }

    @Override
    public Source<ORIG_TYPE> apply(Source<ORIG_TYPE> source) {
        return new AbstractSource<ORIG_TYPE>(source.getName(), null, null) {

            @Override
            public List<FieldDescription<?>> getProvidedFields() {
                return source.getProvidedFields();
            }

            @Override
            public <V> Function<ORIG_TYPE,V> getExtractor(FieldDescription<V> fd){
                return source.getExtractor(fd);
            }

            @Override
            public Stream<ORIG_TYPE> stream() {
                return source.stream().filter(predicate);
            }
        };
    }
}
