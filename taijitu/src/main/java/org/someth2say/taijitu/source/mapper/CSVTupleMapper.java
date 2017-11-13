package org.someth2say.taijitu.source.mapper;

import org.someth2say.taijitu.source.Source;
import org.someth2say.taijitu.tuple.FieldDescription;
import org.someth2say.taijitu.tuple.Tuple;

import java.util.List;
import java.util.stream.Stream;

public class CSVTupleMapper extends AbstractSourceMapper<Object[],Tuple> {

	public static final String NAME="csvToTuple";
	
    public CSVTupleMapper() {
        super();
    }

	@Override
	public String getName() {
		return CSVTupleMapper.NAME;
	}

    private Tuple applyItem(Object[] csvEntry){
        return new Tuple(csvEntry);
    }

    @Override
    public Source<Tuple> apply(Source<Object[]> source) {
        return new Source<Tuple>() {

            @Override
            public List<FieldDescription> getProvidedFields() {
                return source.getProvidedFields();
            }

            @Override
            public Stream<Tuple> stream() {
                return source.stream().map(o -> applyItem(o));
            }

            @Override
            public void close() throws ClosingException {
                source.close();
            }

            @Override
            public String getName() {
                return source.getName();
            }
        };
    }
}
