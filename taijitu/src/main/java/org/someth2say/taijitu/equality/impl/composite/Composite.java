package org.someth2say.taijitu.equality.impl.composite;

import org.someth2say.taijitu.equality.aspects.external.Equalizer;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Composite<T,E extends Equalizer<T>> implements IComposite<E> {

    private final List<E> components;

    public Composite(List<E> components) {
        this.components = components;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()
                + components.stream().map(Equalizer::toString).collect(Collectors.joining(",","(",")"));
    }

    @Override
    public Stream<E> getComponents() {
        return components.parallelStream();
    }

}

interface IComposite<E> {
    Stream<E> getComponents();
}