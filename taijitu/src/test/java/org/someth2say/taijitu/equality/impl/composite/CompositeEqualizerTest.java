package org.someth2say.taijitu.equality.impl.composite;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.someth2say.taijitu.equality.impl.composite.TestComposite.testClassOneTwoEquality;

public class CompositeEqualizerTest {
    @Test
    public void testCompositeEquality() {
        TestComposite tc_aa1 = new TestComposite("aaa", "aaa", 1);
        TestComposite tc_bc1 = new TestComposite("bbb", "ccc", 1);
        TestComposite tc_aA1 = new TestComposite("aaa", "AAA", 1);

        assertFalse(testClassOneTwoEquality.areEquals(tc_aa1, tc_bc1));
        assertTrue(testClassOneTwoEquality.areEquals(tc_aa1, tc_aA1));

    }
}
