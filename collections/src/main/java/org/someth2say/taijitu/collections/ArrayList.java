package org.someth2say.taijitu.collections;

import org.someth2say.taijitu.equality.aspects.external.Equalizer;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

public class ArrayList<E> extends java.util.ArrayList<E> {

    /**
     *
     */
    private static final long serialVersionUID = -5613438747359438122L;
    final Equalizer<E> equalizer;

    public ArrayList(int initialCapacity) {
        super(initialCapacity);
        equalizer = null;
    }

    public ArrayList() {
        super();
        equalizer = null;
    }

    public ArrayList(Collection<? extends E> c) {
        super(c);
        equalizer = null;
    }


    public ArrayList(Equalizer<E> equalizer, int initialCapacity) {
        super(initialCapacity);
        this.equalizer = equalizer;
    }

    public ArrayList(Equalizer<E> equalizer) {
        super();
        this.equalizer = equalizer;
    }

    public ArrayList(Equalizer<E> equalizer, Collection<? extends E> c) {
        super(c);
        this.equalizer = equalizer;
    }

    @Override
    public int indexOf(Object o) {
        if (equalizer == null)
            return super.indexOf(o);
        else {
            try {
                @SuppressWarnings("unchecked") E e = (E) o;
                for (int i = 0; i < size(); i++)
                    // get(i) performs an unnecessary range check...
                    if (equalizer.areEquals(e, get(i)))
                        return i;
            } catch (ClassCastException e) {
                // Object provided can't even be in the collection!
                return -1;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if (equalizer == null || o == null)
            return super.lastIndexOf(o);
        else {
            try {
                @SuppressWarnings("unchecked") E e = (E) o;
                for (int i = size() - 1; i >= 0; i--)
                    // get(i) performs an unnecessary range check...
                    if (equalizer.areEquals(e, get(i)))
                        return i;
            } catch (ClassCastException e) {
                // Object provided can't even be in the collection!
                return -1;
            }
        }
        return -1;
    }

    @Override
    public Object clone() {
        ArrayList<E> result = new ArrayList<>(equalizer, size());
        result.addAll(this);
        return result;
    }

    @Override
    public boolean remove(Object o) {
        if (equalizer == null || o == null)
            return super.remove(o);
        else {
            try {
                @SuppressWarnings("unchecked") E e = (E) o;
                for (int index = 0; index < size(); index++)
                    // remove(index) and get(index) performs an unnecessary range checks...
                    if (equalizer.areEquals(e, get(index))) {
                        remove(index);
                        return true;
                    }
            } catch (ClassCastException e) {
                // Object provided can't even be in the collection!
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (equalizer == null || o == null)
            return super.equals(o);
        if (!(o instanceof List))
            return false;

        ListIterator<E> e1 = listIterator();
        ListIterator<?> e2 = ((List<?>) o).listIterator();
        while (e1.hasNext() && e2.hasNext()) {
            E o1 = e1.next();
            try {
                @SuppressWarnings("unchecked") E o2 = (E) e2.next();
                if (!equalizer.areEquals(o1, o2))
                    return false;
            } catch (ClassCastException e) {
                return false;
            }
        }
        return !(e1.hasNext() || e2.hasNext());

    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),equalizer);
    }


}
