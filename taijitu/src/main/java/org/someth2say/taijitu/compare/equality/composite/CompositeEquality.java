package org.someth2say.taijitu.compare.equality.composite;

import org.someth2say.taijitu.compare.equality.Equality;
import org.someth2say.taijitu.compare.equality.composite.eae.ExtractorAndEquality;
import org.someth2say.taijitu.compare.result.Mismatch;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This is the simplest class of CompositeEquality. It assumes that:
 * <OL><LI>Both sides of equality are the same type and</LI>
 * <li>Both sides of equality are using the same extractors</li></OL>
 *
 * @param <T>
 */
public class CompositeEquality<T> extends AbstractCompositeEquality<T, ExtractorAndEquality<T, ?>> implements Equality<T> {

    public CompositeEquality(List<ExtractorAndEquality<T, ?>> eaes) {
        super(eaes);
    }

    /**
     * Default equality is ordered given the list of extractors.
     */
    @Override
    public boolean equals(T first, T second) {
        return getExtractorsAndEqualities().stream().allMatch(eae -> valueEquals(first, second, eae));
    }

    @Override
    public List<Mismatch> differences(T t1, T t2) {
        return getExtractorsAndEqualities().stream().map(eae -> difference(t1, t2, eae)).filter(Objects::nonNull).collect(Collectors.toList());
    }

}
