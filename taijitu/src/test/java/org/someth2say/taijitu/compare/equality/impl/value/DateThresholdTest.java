package org.someth2say.taijitu.compare.equality.impl.value;

import org.junit.Test;
import org.someth2say.taijitu.compare.equality.wrapper.IComparableHashableWrapper;

import java.util.Date;

import static org.junit.Assert.*;

public class DateThresholdTest {

    DateThreshold<Date> instance = new DateThreshold<>();
    Date now = new Date();
    Date sameSecond = new Date(now.getTime()+900);
    Date tomorrow = new Date(now.getTime()-1000*60*24);
    IComparableHashableWrapper<Date, ?> nowWrap = instance.wrap(now);
    IComparableHashableWrapper<Date, ?> sameWrap = instance.wrap(sameSecond);
    IComparableHashableWrapper<Date, ?> tomWrap = instance.wrap(tomorrow);

    @Test
    public void hashCodeTest() {
        assertEquals(instance.hashCode(now), instance.hashCode(sameSecond));
        assertNotEquals(instance.hashCode(now), instance.hashCode(tomorrow));

        assertEquals(nowWrap.hashCode(), sameWrap.hashCode());

    }

    @Test
    public void equalsTest() {
        assertTrue(instance.equals(now, sameSecond));
        assertFalse(instance.equals(now, tomorrow));

        assertTrue(nowWrap.equalsTo(sameWrap));
        assertFalse(nowWrap.equals(tomWrap));
    }

    @Test
    public void compareTest() {
        assertTrue(instance.compare(now, sameSecond) == 0);
        assertFalse(instance.compare(now, tomorrow) < 0);
        assertFalse(instance.compare(tomorrow, sameSecond) > 0);

        assertTrue(nowWrap.compareTo(sameWrap) == 0);
        assertFalse(nowWrap.compareTo(tomWrap) < 0);
        assertFalse(tomWrap.compareTo(sameWrap) > 0);
    }
}