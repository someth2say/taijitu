package org.someth2say.taijitu.compare.equality.impl.stream;

import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;

import java.util.stream.Stream;

/**
 * Created by Jordi Sola on 16/02/2017.
 */
//TODO: What about StreamComparer<T> or StreamHasher<T>?
public interface StreamEqualizer<T> extends Equalizer<Stream<T>> {

}
