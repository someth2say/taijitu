package org.someth2say.taijitu.compare.equality.impl.value;

import org.junit.Test;
import org.someth2say.taijitu.compare.equality.wrapper.ComparableHashableWrapper;

import static org.junit.Assert.*;

public class NumberThresholdTest {
    final NumberThreshold<Number> instance = NumberThreshold.EQUALITY;
    final ComparableHashableWrapper.Factory<Number> factory = new ComparableHashableWrapper.Factory<>(instance);
    private final Number one = 1.000;
    private final Number oneOOtwo = 1.002;
    private final Number two = 2.000;
    private final ComparableHashableWrapper<Number, ?> oneWrap = factory.wrapp(one);
    private final ComparableHashableWrapper<Number, ?> oneOOTwoWrap = factory.wrapp(oneOOtwo);
    private final ComparableHashableWrapper<Number, ?> twoWrap = factory.wrapp(two);

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