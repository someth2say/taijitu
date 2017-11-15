package org.someth2say.taijitu.source.csv;

import org.someth2say.taijitu.config.ConfigurationLabels;
import org.someth2say.taijitu.config.interfaces.ISourceCfg;
import org.someth2say.taijitu.source.AbstractSource;
import org.someth2say.taijitu.source.FieldDescription;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Stream;

public class CSVResourceSource extends AbstractSource<Object[]> {
    public static final String NAME = "csv";
    public static final String FIELD_SEPARATOR = ",";
    private Stream<String> lines;
    private List<FieldDescription<?>> providedFields;

    @Override
    public void close() throws ClosingException {
        if (lines != null) lines.close();
    }

    @Override
    public Class<Object[]> getTypeParameter() {
        return Object[].class;
    }

    private static class BuildProperties {
        final String path;

        BuildProperties(Properties properties) {
            this.path = properties.getProperty(ConfigurationLabels.Comparison.RESOURCE);
        }
    }

    //TODO: this properties should be created externally to the source
    private final BuildProperties buildProperties;

    public CSVResourceSource(final ISourceCfg iSource) throws IOException, URISyntaxException {
        super(iSource);
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
    public List<FieldDescription<?>> getProvidedFields() {
        if (this.providedFields == null) {
            try (Stream<String> lines = getLines()) {
                if (lines != null) {
                    Optional<String> headerOpt = lines.findFirst();
                    if (headerOpt.isPresent()) {
                        String header = headerOpt.get();
                        String[] split = header.split(FIELD_SEPARATOR);
                        this.providedFields = new ArrayList<>(split.length);
                        for (String s : split) {
                            //TODO: Maybe we can try somehow to provide a class (i.e. by parsing the header...), or use String instead of null...
                            this.providedFields.add(new FieldDescription(s, null));
                        }
                        return this.providedFields;
                    }
                } else { // Can't get lines... but this can drive the iterator to enter here for each entry!!!
                    return null;
                }
            }
        }
        return this.providedFields;

    }

    @Override
    public <V> Function<Object[], V> getExtractor(FieldDescription<V> fd) {
        int index = getProvidedFields().indexOf(fd);
        if (index < 0) return null;
        return (Object[] obj) -> (V) obj[index];
    }

    @Override
    public Stream<Object[]> stream() {
        lines = getLines();
        if (lines != null) {
            return lines.skip(1).map(l -> l.split(FIELD_SEPARATOR));
        } else {
            return null;
        }
    }

}