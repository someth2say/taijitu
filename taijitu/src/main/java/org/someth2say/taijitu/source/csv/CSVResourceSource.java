package org.someth2say.taijitu.source.csv;

import java.io.*;
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
import org.someth2say.taijitu.source.AbstractSource;
import org.someth2say.taijitu.tuple.ComparableTuple;
import org.someth2say.taijitu.tuple.FieldDescription;

public class CSVResourceSource extends AbstractSource {
    public static final String NAME = "csv";
    public static final String FIELD_SEPARATOR = ",";
    private Stream<String> lines;

    @Override
    public void close() throws ClosingException {
        if (lines != null) lines.close();
    }

    private static class BuildProperties {
        final String path;

        BuildProperties(Properties properties) {
            this.path = properties.getProperty(ConfigurationLabels.Comparison.RESOURCE);
        }
    }

    @Override
    public ISourceCfg getConfig() {
        return iSource;
    }

    private CSVTupleBuilder builder;
    private final BuildProperties buildProperties;

    public CSVResourceSource(final ISourceCfg iSource, final IComparisonCfg iComparisonCfg, final ComparisonContext context) throws IOException, URISyntaxException {
        super(iSource, iComparisonCfg, context);
        this.buildProperties = new BuildProperties(iSource.getBuildProperties());
    }

    private Stream<String> getLines() {
        try {
            URL resource = new URL(buildProperties.path);
            InputStream in = resource.openStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            return bufferedReader.lines();
        } catch (IOException e) {
            // Try as file
            try {
                URL resource = CSVResourceSource.class.getResource(buildProperties.path); //That will be null upon wrong file
                if (resource != null) {
                    Path path = Paths.get(resource.toURI());
                    return Files.lines(path);
                } else {
                    return null;
                }
            } catch (IOException | URISyntaxException e2) {
                return null;
            }
        }
    }

    @Override
    public List<FieldDescription> getFieldDescriptions() {
        Stream<String> lines = getLines();
        if (lines != null) {
            Optional<String> headerOpt = lines.findFirst();
            if (headerOpt.isPresent()) {
                String header = headerOpt.get();
                String[] split = header.split(FIELD_SEPARATOR);
                List<FieldDescription> result = new ArrayList<>(split.length);
                for (String s : split) {
                    //TODO: Maybe we can try somehow to provide a class (i.e. by parsing the header...)
                    result.add(new FieldDescription(position, s, null));
                }
                return result;
            }
            lines.close();
        }
        return null;
    }

    @Override
    public Iterator<ComparableTuple> iterator() {
        lines = getLines();
        if (lines != null) {
            return lines.skip(1).map(s -> getTupleBuilder().apply(s)).iterator();
        } else {
            return null;
        }

    }

    private CSVTupleBuilder getTupleBuilder() {
        if (builder == null) {
            final FieldMatcher matcher = MatcherRegistry.getMatcher(iComparisonCfg.getMatchingStrategyName());
            builder = new CSVTupleBuilder(matcher, context, iSource.getName());
        }
        return builder;
    }

    @Override
    public String getName() {
        return CSVResourceSource.NAME;
    }
}