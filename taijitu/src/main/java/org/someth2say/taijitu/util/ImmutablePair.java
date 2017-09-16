package org.someth2say.taijitu.util;

import java.util.Objects;

/**
 * Created by Jordi Solaon 20/03/2017.
 */
public class ImmutablePair<L, R> implements Pair<L, R> {
    private final L left;
    private final R right;

    public ImmutablePair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public L getKey() {
        return left;
    }

    @Override
    public R getValue() {
        return right;
    }

    @Override
    public L getLeft() {
        return left;
    }

    @Override
    public R getRight() {
        return right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImmutablePair<?, ?> that = (ImmutablePair<?, ?>) o;
        return Objects.equals(this.getKey(), that.getKey()) && Objects.equals(this.getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getKey(), this.getValue());
    }
}
