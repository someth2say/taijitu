package org.someth2say.taijitu.compare.equality.impl.value;

import org.junit.Test;
import org.someth2say.taijitu.compare.equality.aspects.external.Comparator;
import org.someth2say.taijitu.compare.equality.wrapper.ComparableHashableWrapper;
import org.someth2say.taijitu.compare.equality.wrapper.ComparableWrapper;

import java.util.Date;

import static org.junit.Assert.*;

public class DateThresholdTest {

    final DateThreshold<Date> instance = DateThreshold.EQUALITY;
    final ComparableWrapper.Factory<Date> factory = new ComparableWrapper.Factory<>(instance);

    final Date now = new Date();
    final Date sameSecond = new Date(now.getTime() + 900);
    final Date tomorrow = new Date(now.getTime() - 1000 * 60 * 24);

    final ComparableWrapper<Date> nowWrap = factory.wrap(now);
    final ComparableWrapper<Date> sameWrap = factory.wrap(sameSecond);
    final ComparableWrapper<Date> tomWrap = factory.wrap(tomorrow);


    @Test
    public void equalsTest() {
        assertTrue(instance.areEquals(now, sameSecond));
        assertFalse(instance.areEquals(now, tomorrow));

        assertTrue(nowWrap.equalsTo(sameWrap));
        assertFalse(nowWrap.equals(tomWrap));
    }

    @Test
    public void compareTest() {
        assertTrue(instance.compare(now, sameSecond) == 0);
        assertTrue(instance.compare(now, tomorrow) > 0);
        assertTrue(instance.compare(tomorrow, sameSecond) < 0);

        assertTrue(nowWrap.compareTo(sameWrap) == 0);
        assertTrue(nowWrap.compareTo(tomWrap) > 0);
        assertTrue(tomWrap.compareTo(sameWrap) < 0);
    }
}