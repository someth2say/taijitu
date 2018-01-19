package org.someth2say.taijitu.compare.equality.proxy;

import org.junit.Test;
import org.someth2say.taijitu.compare.equality.aspects.external.Comparator;
import org.someth2say.taijitu.compare.equality.impl.value.DateThreshold;
import org.someth2say.taijitu.compare.equality.impl.value.NumberThreshold;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.*;

public class ProxyFactoryTest {
    @Test
    public void proxyEqualizerOnClass() {
        Date now = new Date();
        Date future = new Date(now.getTime() + 400);
        Date nowProxy = ProxyFactory.proxyEqualizer(now, DateThreshold.EQUALITY, Date.class);
        Date futureProxy = ProxyFactory.proxyEqualizer(future, DateThreshold.EQUALITY, Date.class);

        assertTrue(nowProxy instanceof Date);
        assertEquals(nowProxy, future); // Test equality is actually applied
        assertNotEquals(future, nowProxy); //Equality Proxy can not ensure equality reflexiveness... and this may be a real pain!

        Collection<Date> dates = Collections.singletonList(nowProxy);
        assertTrue(dates.contains(now)); // Succeeds because now.equals(nowProxy)==true
        assertFalse(dates.contains(future)); // Fails because Collection future.equals(nowProxy)==false;
        assertTrue(dates.contains(futureProxy)); // Succeeds because futureProxy.equals(nowProxy)==true

        dates = Collections.singletonList(now);
        assertTrue(dates.contains(nowProxy)); // Succeeds because nowProxy.equals(now)==true
        assertFalse(dates.contains(future)); // Fails because Collection future.equals(now)==false;
        assertTrue(dates.contains(futureProxy)); // Succeeds because futureProxy.equals(now)==true

        assertEquals(now.toString(), nowProxy.toString()); // Test non-equality methods are kept
    }

    @Test
    public void proxyEqualizerOnInterface() {
        Number low = 1.001f;
        Number up = 1.002f;
        Comparator<Number> comparator = NumberThreshold.EQUALITY;
        Number lowProxy = ProxyFactory.proxyComparator(low, comparator, Number.class);
        Number upProxy = ProxyFactory.proxyComparator(up, comparator, Number.class);

        assertTrue(lowProxy instanceof Number);
        assertEquals(lowProxy, up); // Test equality is actually applied
        assertNotEquals(up, lowProxy); //Equality Proxy can not ensure equality reflexiveness... and this may be a real pain!

        Collection<Number> dates = Collections.singletonList(lowProxy);
        assertFalse(dates.contains(low)); // Fails because low.equals(lowProxy)==false (Number is not an instance of Float)
        assertFalse(dates.contains(up)); // Fails because Collection up.equals(lowProxy)==false; (Number is not an instance of Float)
        assertTrue(dates.contains(upProxy)); // Succeeds because upProxy.equals(lowProxy)==true

        dates = Collections.singletonList(low);
        assertTrue(dates.contains(lowProxy)); // Succeeds because lowProxy.equals(low)==true
        assertFalse(dates.contains(up)); // Fails because Collection up.equals(low)==false;
        assertTrue(dates.contains(upProxy)); // Succeeds because upProxy.equals(low)==true
    }


}
