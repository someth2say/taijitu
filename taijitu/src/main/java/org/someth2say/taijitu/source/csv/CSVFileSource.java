package org.someth2say.taijitu.source.csv;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import org.someth2say.taijitu.ComparisonContext;
import org.someth2say.taijitu.config.ConfigurationLabels;
import org.someth2say.taijitu.config.interfaces.IComparisonCfg;
import org.someth2say.taijitu.config.interfaces.ISourceCfg;
import org.someth2say.taijitu.matcher.FieldMatcher;
import org.someth2say.taijitu.registry.MatcherRegistry;
import org.someth2say.taijitu.source.Source;
import org.someth2say.taijitu.tuple.ComparableTuple;
import org.someth2say.taijitu.tuple.FieldDescription;
import org.someth2say.taijitu.tuple.builder.CSVTupleBuilder;

public class CSVFileSource implements Source {
    public static final String NAME = "csv";

    private static class BuildProperties {
        final String path;

        BuildProperties(Properties properties) {
            this.path = properties.getProperty(ConfigurationLabels.Comparison.FILE_PATH);
        }
    }

    //TODO: Considering adding an the last exception raised, so we can check the status.
    private final IComparisonCfg comparisonConfigIface;
    private final ComparisonContext context;

    @Override
    public ISourceCfg getConfig() {
        return fileConfig;
    }

    private final ISourceCfg fileConfig;
    private CSVTupleBuilder builder;
    private final Stream<String> lineStream;

    public CSVFileSource(final ISourceCfg iSource, final IComparisonCfg comparisonConfigIface, final ComparisonContext context) throws IOException, URISyntaxException {
        this.fileConfig = iSource;
        this.comparisonConfigIface = comparisonConfigIface;
        this.context = context;

        BuildProperties buildProperties = new BuildProperties(iSource.getBuildProperties());

        URL resource = CSVFileSource.class.getResource(buildProperties.path);
        File file = new File(resource.toURI());
        this.lineStream = Files.lines(file.toPath());
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
            final FieldMatcher matcher = MatcherRegistry.getMatcher(comparisonConfigIface.getMatchingStrategyName());
            builder = new CSVTupleBuilder(matcher, context, fileConfig.getName());
        }
        return builder;
    }

    @Override
    public String getName() {
        return CSVFileSource.NAME;
    }
}