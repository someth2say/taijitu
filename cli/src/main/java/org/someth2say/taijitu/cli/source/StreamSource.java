package org.someth2say.taijitu.cli.source;

import org.apache.commons.lang3.StringUtils;
import org.someth2say.taijitu.cli.config.ConfigurationLabels;
import org.someth2say.taijitu.cli.config.DefaultConfig;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * The most basic source, just a wrapper for a stream.
 * It will only define a single field (the elements for the stream) and a single extractor (the identity function).
 * @param <T>
 */
public class StreamSource<T> extends AbstractSource<T> {

    private final List<FieldDescription<?>> providedFields;
    private final Stream<T> source;
    private final FieldDescription<T> fd;

    public StreamSource(String name, Properties buildProperties, Properties fetchProperties) {
        super(name, buildProperties, fetchProperties);
        String[] strings = StringUtils.split(buildProperties.getProperty(ConfigurationLabels.SOURCE_BUILD_PROPERTIES, ""), DefaultConfig.DEFAULT_LIST_DELIMITER);
        this.source = (Stream<T>) Stream.of(strings);
        fd = (FieldDescription<T>) new FieldDescription<>(null, String.class);
        providedFields = Collections.singletonList(fd);
    }

    public StreamSource(String name, Stream<T> source, Class<T> clazz) {
        super(name, null,null);
        this.source = source;
        fd = new FieldDescription<>(null, clazz);
        providedFields = Collections.singletonList(fd);
    }

    @Override
    public List<FieldDescription<?>> getProvidedFields() {
        return providedFields;
    }

    @Override
    public <V> Function<T, V> getExtractor(FieldDescription<V> fd) {
        return this.fd == fd ? (T t)->(V)t : null;
    }

    @Override
    public Stream<T> stream() {
        return source;
    }

}
