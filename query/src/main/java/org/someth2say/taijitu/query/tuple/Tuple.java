package org.someth2say.taijitu.query.tuple;

import java.util.Arrays;
import java.util.Iterator;

/**
 *
 */
//TODO: Please, do not re-invent array...
public abstract class Tuple {
    private final Object[] columns;

    public Tuple(Object[] columns) {
        this.columns = columns;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.columns);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        } else {
            Tuple other = (Tuple) obj;
            return Arrays.equals(this.columns, other.columns);
        }
    }

    //@Override
    public Object getValue(int pos) {
        return this.columns[pos];
    }

    //@Override
    public Iterator<Object> iterator() {
        return Arrays.asList(this.columns).iterator();
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
        return this.columns.length;
    }

}
