package org.someth2say.taijitu.compare.equality.impl.composite;

import org.slf4j.Logger;

import java.util.List;

public interface IComposite {
    List<ExtractorAndEquality> getExtractorsAndEqualities();

    Logger getLogger();

}
