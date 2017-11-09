package org.someth2say.taijitu.tuple;

import java.util.Objects;

//TODO: Make FieldDescription generic, so can match types on compile time
public class FieldDescription {
    private final String name;
    private final String clazz;

    public FieldDescription(String name, String clazz) {
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

    public String getClazz() {
        return clazz;
    }

    @Override
    public String toString() {
        return name + "(" + clazz + ")";
    }

}
