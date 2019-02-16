package org.someth2say.taijitu.equality.impl.composite;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.someth2say.taijitu.equality.impl.composite.TestComposite.testClassOneTwoEquality;

public class CompositeEqualizerTest {
    @Test
    public void testCompositeEquality() {
        TestComposite as = new TestComposite("aaa", "aaa", 1);
        TestComposite bs = new TestComposite("bbb", "ccc", 1);
        TestComposite moreAs = new TestComposite("aaa", "AAA", 1);

        assertFalse(testClassOneTwoEquality.areEquals(as, bs));
        assertTrue(testClassOneTwoEquality.areEquals(as, moreAs));

    }
}
