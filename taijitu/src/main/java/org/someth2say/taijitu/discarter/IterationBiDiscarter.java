package org.someth2say.taijitu.discarter;

import java.util.function.BiConsumer;

/**
 * @author Jordi Sola
 */
class IterationBiDiscarter<T, U> implements BiConsumer<T, U> {
    private final long iterations;
    private final BiConsumer<T, U> biConsumer;
    private long iterationsRemaining = 0L;

    public IterationBiDiscarter(long _iterations, BiConsumer<T, U> biConsumer) {
        this.iterationsRemaining = _iterations;
        this.iterations = _iterations;
        this.biConsumer = biConsumer;
    }

    @Override
    public void accept(T t, U u) {
        --this.iterationsRemaining;
        if (this.iterationsRemaining <= 0L) {
            this.iterationsRemaining = this.iterations;
            biConsumer.accept(t, u);
        }

    }
}
