package org.someth2say.taijitu.tuple;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Abstract concept of a Tuple, that is, a sorted collection of objects.
 *
 * @author Jordi Sola
 */
public abstract class Tuple {
    private final Object[] fieldValues;

    public Tuple(Object[] fieldValues) {
        this.fieldValues = fieldValues;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.fieldValues);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        } else {
            Tuple other = (Tuple) obj;
            return Arrays.equals(this.fieldValues, other.fieldValues);
        }
    }

    public Object getValue(int pos) {
        return this.fieldValues[pos];
    }

    public Iterator<Object> iterator() {
        return Arrays.asList(this.fieldValues).iterator();
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        Iterator<Object> it = this.iterator();
        while (it.hasNext()) {
            Object value = it.next();
            if (value != null) {
                String stringValue = value.toString();
                stringValue = stringValue.replaceAll(",", ".");
                buf.append(stringValue);
            }

            if (it.hasNext()) {
                buf.append(",");
            }
        }

        return buf.toString();
    }

    public int size() {
        return this.fieldValues.length;
    }

}