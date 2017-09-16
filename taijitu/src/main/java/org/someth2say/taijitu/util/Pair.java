package org.someth2say.taijitu.util;

/**
 * Created by Jordi Sola on 20/03/2017.
 */
public interface Pair<L, R> {

    L getKey();

    R getValue();

    L getLeft();

    R getRight();
}
