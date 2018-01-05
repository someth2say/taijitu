package org.someth2say.taijitu.cli.source.mapper;

import org.junit.Test;
import org.someth2say.taijitu.cli.source.Source;
import org.someth2say.taijitu.cli.source.StreamSource;
import org.someth2say.taijitu.compare.equality.aspects.external.Comparator;
import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.compare.equality.impl.stream.StreamEqualizer;
import org.someth2say.taijitu.compare.equality.impl.stream.simple.SimpleStreamEqualizer;
import org.someth2say.taijitu.compare.equality.impl.stream.sorted.ComparableStreamEqualizer;
import org.someth2say.taijitu.compare.equality.impl.value.JavaComparable;
import org.someth2say.taijitu.compare.equality.impl.value.JavaObject;
import org.someth2say.taijitu.compare.result.Difference;
import org.someth2say.taijitu.compare.result.Missing;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class FilterMapperTest {
    @Test
    public void testFilterMapper() throws Source.ClosingException {
        FilterMapper<Integer> stringFilterMapper = new FilterMapper<>(s -> !s.equals(3));

        Source<Integer> source1 = new StreamSource<>("", Stream.of(1, 2, 3, 4, 5), Integer.class);
        Source<Integer> source2 = new StreamSource<>("", Stream.of(1, 2, 3, 4, 5), Integer.class);
        Source<Integer> filteredSource1 = stringFilterMapper.apply(source1);

        Comparator<Integer> comparator = new JavaComparable<>();

        StreamEqualizer<Integer> sse = new ComparableStreamEqualizer<>(comparator);
        Stream<Difference<?>> diffs = sse.underlyingDiffs(filteredSource1.stream(), source2.stream());
        List<Difference<?>> collect = diffs.collect(Collectors.toList());
        assertEquals(1, collect.size());
        assertEquals(new Missing<>(comparator, 3), collect.get(0));

    }
}