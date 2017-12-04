package org.someth2say.taijitu.compare.equality.impl.composite;

import org.someth2say.taijitu.compare.equality.aspects.external.HasherEqualizer;

import java.util.function.Function;

public interface ICompositeHasherEqualizer<T> extends ICompositeEqualizer<T>, HasherEqualizer<T> {

    @Override
    default int hashCode(T obj) {
        int result = 1;
        for (ExtractorAndEquality eae : getExtractorsAndEqualities()) {
            result = 31 * result + valueHashCode(obj, eae);
        }
        return result;
    }

    default <V> int valueHashCode(T obj, ExtractorAndEquality<T, V, HasherEqualizer<V>> eae) {
        Function<T, V> key = eae.getExtractor();
        V value = key.apply(obj);
        return eae.getEquality().hashCode(value);
    }

}
