package org.someth2say.taijitu.compare.equality.composite;

import org.someth2say.taijitu.compare.equality.CategorizerEquality;
import org.someth2say.taijitu.compare.equality.ComparableCategorizerEquality;

import java.util.function.Function;

public interface ICompositeCategorizer<T> extends IComposite, CategorizerEquality<T>{

    @Override
    default int hashCode(T obj) {
        int result = 1;
        for (ExtractorAndEquality eae : getExtractorsAndEqualities()) {
            result = 31 * result + valueHashCode(obj, eae);
        }
        return result;
    }

    default <V> int valueHashCode(T obj, ExtractorAndEquality<T, V, CategorizerEquality<V>> eae) {
        Function<T, V> key = eae.getExtractor();
        V value = key.apply(obj);
        return eae.getEquality().hashCode(value);
    }

}
