package org.someth2say.taijitu.compare.equality.impl.value;

import org.junit.Test;
import org.someth2say.taijitu.compare.equality.wrapper.IComparableHashableWrapper;

import static org.junit.Assert.*;

public class NumberThresholdTest {
    NumberThreshold<Number> instance = new NumberThreshold<>();
    private Number one = 1.000;
    private Number oneOOtwo = 1.002;
    private Number two = 2.000;
    private IComparableHashableWrapper<Number, ?> oneWrap = instance.wrap(one);
    private IComparableHashableWrapper<Number, ?> oneOOTwoWrap = instance.wrap(oneOOtwo);
    private IComparableHashableWrapper<Number, ?> twoWrap = instance.wrap(two);


    @Test
    public void hashCodeTest() {
        assertEquals(instance.hashCode(one), instance.hashCode(oneOOtwo));
        assertNotEquals(instance.hashCode(one), instance.hashCode(two));

        assertEquals(oneWrap.hashCode(), oneOOTwoWrap.hashCode());
        assertNotEquals(oneWrap.hashCode(), twoWrap.hashCode());

    }

    @Test
    public void equalsTest() {
        assertTrue(instance.equals(one, oneOOtwo));
        assertFalse(instance.equals(one, two));

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