package org.someth2say.taijitu.collections;

import org.someth2say.taijitu.compare.equality.aspects.external.Hasher;

import java.util.Collection;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;

public class LinkedHashSet<E>
        extends HashSet<E>
        implements Set<E>, Cloneable, java.io.Serializable {

    private static final long serialVersionUID = -2851667679971038690L;

    /**
     * Constructs a new, empty linked hash set with the specified initial
     * capacity and load factor.
     *
     * @param initialCapacity the initial capacity of the linked hash set
     * @param loadFactor      the load factor of the linked hash set
     * @param hasher
     * @throws IllegalArgumentException if the initial capacity is less
     *                                  than zero, or if the load factor is nonpositive
     */
    public LinkedHashSet(int initialCapacity, float loadFactor, Hasher<E> hasher) {
        super(initialCapacity, loadFactor, true, hasher);
    }

    /**
     * Constructs a new, empty linked hash set with the specified initial
     * capacity and the default load factor (0.75).
     *
     * @param initialCapacity the initial capacity of the LinkedHashSet
     * @param hasher
     * @throws IllegalArgumentException if the initial capacity is less
     *                                  than zero
     */
    public LinkedHashSet(int initialCapacity, Hasher<E> hasher) {
        super(initialCapacity, .75f, true, hasher);
    }

    /**
     * Constructs a new, empty linked hash set with the default initial
     * capacity (16) and load factor (0.75).
     *
     * @param hasher
     */
    public LinkedHashSet(Hasher<E> hasher) {
        super(16, .75f, true, hasher);
    }

    /**
     * Constructs a new linked hash set with the same elements as the
     * specified collection.  The linked hash set is created with an initial
     * capacity sufficient to hold the elements in the specified collection
     * and the default load factor (0.75).
     *
     * @param c      the collection whose elements are to be placed into
     *               this set
     * @param hasher
     * @throws NullPointerException if the specified collection is null
     */
    public LinkedHashSet(Collection<? extends E> c, Hasher<E> hasher) {
        super(Math.max(2 * c.size(), 11), .75f, true, hasher);
        addAll(c);
    }

    /**
     * Creates a <em><a href="Spliterator.html#binding">late-binding</a></em>
     * and <em>fail-fast</em> {@code Spliterator} over the elements in this set.
     * <p>
     * <p>The {@code Spliterator} reports {@link Spliterator#SIZED},
     * {@link Spliterator#DISTINCT}, and {@code ORDERED}.  Implementations
     * should document the reporting of additional characteristic values.
     *
     * @return a {@code Spliterator} over the elements in this set
     * @implNote The implementation creates a
     * <em><a href="Spliterator.html#binding">late-binding</a></em> spliterator
     * from the set's {@code Iterator}.  The spliterator inherits the
     * <em>fail-fast</em> properties of the set's iterator.
     * The created {@code Spliterator} additionally reports
     * {@link Spliterator#SUBSIZED}.
     * @since 1.8
     */
    @Override
    public Spliterator<E> spliterator() {
        return Spliterators.spliterator(this, Spliterator.DISTINCT | Spliterator.ORDERED);
    }
}
