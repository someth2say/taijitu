package org.someth2say.taijitu.cli.source.mapper;

import org.junit.Test;
import org.someth2say.taijitu.cli.source.Source;
import org.someth2say.taijitu.cli.source.StreamSource;
import org.someth2say.taijitu.equality.aspects.external.Comparator;
import org.someth2say.taijitu.stream.StreamEqualizer;
import org.someth2say.taijitu.stream.sorted.ComparableStreamEqualizer;
import org.someth2say.taijitu.equality.impl.value.ComparableComparatorHasher;
import org.someth2say.taijitu.equality.explain.Difference;
import org.someth2say.taijitu.equality.explain.Missing;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class FilterMapperTest {
    @Test
    public void testFilterMapper() {
        FilterMapper<Integer> stringFilterMapper = new FilterMapper<>(s -> !s.equals(3));

        Source<Integer> source1 = new StreamSource<>("", Stream.of(1, 2, 3, 4, 5), Integer.class);
        Source<Integer> source2 = new StreamSource<>("", Stream.of(1, 2, 3, 4, 5), Integer.class);
        Source<Integer> filteredSource1 = stringFilterMapper.apply(source1);

        Comparator<Integer> comparator = new ComparableComparatorHasher<>();

        StreamEqualizer<Integer> sse = new ComparableStreamEqualizer<>(comparator);
        Stream<Difference<Integer>> diffs = sse.explain(filteredSource1.stream(), source2.stream());
        List<Difference<Integer>> collect = diffs.collect(Collectors.toList());
        assertEquals(1, collect.size());
        assertEquals(new Missing<>(comparator, 3), collect.get(0));

    }
}