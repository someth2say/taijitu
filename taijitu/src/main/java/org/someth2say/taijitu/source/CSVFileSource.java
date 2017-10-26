package org.someth2say.taijitu.source;

import org.apache.log4j.Logger;
import org.someth2say.taijitu.ComparisonContext;
import org.someth2say.taijitu.config.ComparisonConfig;
import org.someth2say.taijitu.config.FileSourceConfig;
import org.someth2say.taijitu.config.SourceConfig;
import org.someth2say.taijitu.matcher.FieldMatcher;
import org.someth2say.taijitu.registry.MatcherRegistry;
import org.someth2say.taijitu.tuple.ComparableTuple;
import org.someth2say.taijitu.tuple.FieldDescription;
import org.someth2say.taijitu.tuple.builder.CSVTupleBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class CSVFileSource implements Source {
    private static final Logger logger = Logger.getLogger(CSVFileSource.class);

    //TODO: Considering adding an the last exception raised, so we can check the status.
    private final ComparisonConfig comparisonConfig;
    private final ComparisonContext context;

    @Override
    public SourceConfig getConfig() {
        return fileConfig;
    }

    private final FileSourceConfig fileConfig;
    private CSVTupleBuilder builder;
    private final Stream<String> lineStream;

    public CSVFileSource(final ComparisonConfig comparisonConfig, final String sourceId, final ComparisonContext context) throws IOException {
        this.comparisonConfig = comparisonConfig;
        //TODO: Ugh.... cast...
        fileConfig = (FileSourceConfig) comparisonConfig.getSourceConfig(sourceId);
        assert fileConfig != null;
        this.context = context;
        this.lineStream = Files.lines(Paths.get(fileConfig.getPath()));
    }

    @Override
    public List<FieldDescription> getFieldDescriptions() {
        Optional<String> headerOpt = lineStream.findFirst();
        if (headerOpt.isPresent()) {
            String header = headerOpt.get();
            String[] split = header.split(",");
            List<FieldDescription> result = new ArrayList<>(split.length);
            for (String s : split) {
                //TODO: Maybe we can try somehow to provide a class (i.e. by parsing the header...)
                result.add(new FieldDescription(s, null));
            }
            return result;
        } else {
            return null;
        }
    }

    @Override
    public Iterator<ComparableTuple> iterator() {
        return lineStream.skip(1).map(s -> getTupleBuilder().apply(s)).iterator();
    }

    private CSVTupleBuilder getTupleBuilder() {
        if (builder == null) {
            final FieldMatcher matcher = MatcherRegistry.getMatcher(comparisonConfig.getMatchingStrategyName());
            builder = new CSVTupleBuilder(matcher, context, fileConfig.getName());
        }
        return builder;
    }

}