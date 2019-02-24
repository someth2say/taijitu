package org.someth2say.taijitu.equality.impl.value;

import org.junit.Test;
import org.someth2say.taijitu.equality.wrapper.ComparableWrapper;
import org.someth2say.taijitu.equality.wrapper.Wrappers;

import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DateThresholdComparatorTest {

    final DateThresholdComparator<Date> instance = DateThresholdComparator.EQUALITY;
    final ComparableWrapper.Factory<Date> factory = Wrappers.factory(instance);

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