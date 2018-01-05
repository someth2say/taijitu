package org.someth2say.taijitu.compare.equality.impl.composite;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractCompositeEquality {

    protected final List<ExtractorAndEquality> extractorsAndEqualities;

    public AbstractCompositeEquality(List<ExtractorAndEquality> eaes) {
        this.extractorsAndEqualities=eaes;
    }

    public List<ExtractorAndEquality> getExtractorsAndEqualities() {
        return extractorsAndEqualities;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() +"("+ extractorsAndEqualities.stream().map(ExtractorAndEquality::toString).collect(Collectors.joining(",")) +")";
    }
}