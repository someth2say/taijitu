package org.someth2say.taijitu.equality.impl.value;

import org.junit.Test;
import org.someth2say.taijitu.equality.wrapper.ComparableHashableWrapper;
import org.someth2say.taijitu.equality.wrapper.Wrappers;

import static org.junit.Assert.*;

public class NumberThresholdComparatorHasherTest {
    final NumberThresholdComparatorHasher<Number> instance = NumberThresholdComparatorHasher.INSTANCE;
    final ComparableHashableWrapper.Factory<Number> factory = Wrappers.factory(instance);

    private final Number one = 1.000;
    private final Number oneOOtwo = 1.002;
    private final Number two = 2.000;

    private final ComparableHashableWrapper<Number> oneWrap = factory.wrap(one);
    private final ComparableHashableWrapper<Number> oneOOTwoWrap = factory.wrap(oneOOtwo);
    private final ComparableHashableWrapper<Number> twoWrap = factory.wrap(two);

    @Test
    public void hashCodeTest() {
        assertEquals(instance.hash(one), instance.hash(oneOOtwo));
        assertNotEquals(instance.hash(one), instance.hash(two));

        assertEquals(oneWrap.hashCode(), oneOOTwoWrap.hashCode());
        assertNotEquals(oneWrap.hashCode(), twoWrap.hashCode());
    }

    @Test
    public void equalsTest() {
        assertTrue(instance.areEquals(one, oneOOtwo));
        assertFalse(instance.areEquals(one, two));

        assertTrue(oneWrap.equalsTo(oneOOTwoWrap));
        assertFalse(oneWrap.equals(twoWrap));
    }

    @Test
    public void compareTest() {
        assertTrue(instance.compare(one, oneOOtwo) == 0);
        assertTrue(instance.compare(one, two) < 0);
        assertTrue(instance.compare(two, oneOOtwo) > 0);

        assertTrue(oneWrap.compareTo(oneOOTwoWrap) == 0);
        assertTrue(oneWrap.compareTo(twoWrap) < 0);
        assertTrue(twoWrap.compareTo(oneOOTwoWrap) > 0);
    }
}