package org.someth2say.taijitu.cli.discarter;

import org.junit.Test;

import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class IterationBiDiscarterTest {

    @Test
    public void accept() {

        StringBuilder s = new StringBuilder("");
        IterationBiDiscarter<String, String> discarter = new IterationBiDiscarter<>(5, (String a, String b) -> s.append(a).append(b));

        IntStream.range(1,20).forEach((i)->discarter.accept(Integer.toString(i),","));

        assertEquals("5,10,15,",s.toString());

    }
}