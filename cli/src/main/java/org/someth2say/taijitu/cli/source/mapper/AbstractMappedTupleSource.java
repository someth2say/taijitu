package org.someth2say.taijitu.cli.source.mapper;

import org.someth2say.taijitu.cli.source.AbstractSource;
import org.someth2say.taijitu.cli.source.FieldDescription;
import org.someth2say.taijitu.cli.source.Source;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class AbstractMappedTupleSource extends AbstractSource<Object[]> {

    private final Map<FieldDescription, Integer> fieldPositions = new HashMap<>();

    AbstractMappedTupleSource(Source<?> source) {
        super(source.getName());
    }

    @Override
    public <V> Function<Object[], V> getExtractor(FieldDescription<V> fd) {
        int index = getFieldPosition(fd);
        if (index < 0) return null;
        return (Object[] tuple) -> (V) tuple[index];
    }

    private <V> int getFieldPosition(FieldDescription<V> fd) {
        return fieldPositions.computeIfAbsent(fd, field -> getProvidedFields().indexOf(field));
    }

    @Override
    public Class<Object[]> getTypeParameter() {
        return Object[].class;
    }

}