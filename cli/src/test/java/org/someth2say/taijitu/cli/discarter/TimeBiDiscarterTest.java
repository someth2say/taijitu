package org.someth2say.taijitu.discarter;

import org.junit.Test;

import static org.junit.Assert.*;

public class TimeBiDiscarterTest {

    @Test
    public void accept() throws InterruptedException {
        StringBuilder s = new StringBuilder("");
        TimeBiDiscarter<String, String> discarter = new TimeBiDiscarter<>(100, (String a, String b) -> s.append(a).append(b));
        discarter.accept("-",","); // This one should be discarded, to close to creation
        Thread.sleep(200);
        discarter.accept("a",","); // This one should be accepted
        discarter.accept("b",","); // This one should be discarded, as too close to previous one
        Thread.sleep(200);
        discarter.accept("c",","); // This one should be accepted
        discarter.accept("d",","); // This one should be discarded, as too close to previous one

        assertEquals("a,c,",s.toString());
    }
}