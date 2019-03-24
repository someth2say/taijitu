package org.someth2say.taijitu.cli.source;

import java.util.Objects;

public class FieldDescription<V> {
    private final String name;
    private final Class<V> clazz;

    public FieldDescription(String name, Class<V> clazz)  {
        this.name = name;
        this.clazz = clazz;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldDescription that = (FieldDescription) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(clazz, that.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, clazz);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + "(" + clazz.getName() + ")";
    }

    public Class<V> getClazz() {
        return clazz;
    }
}
