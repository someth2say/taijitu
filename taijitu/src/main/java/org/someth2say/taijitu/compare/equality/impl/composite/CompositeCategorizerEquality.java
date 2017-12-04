package org.someth2say.taijitu.compare.equality.impl.composite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.someth2say.taijitu.compare.equality.aspects.external.CategorizerEquality;
import org.someth2say.taijitu.compare.equality.aspects.external.Equality;
import org.someth2say.taijitu.compare.equality.impl.value.JavaObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CompositeCategorizerEquality<T> extends AbstractCompositeEquality implements ICompositeCategorizerEquality<T>, ICompositeEquality<T> {
    private static final Logger logger = LoggerFactory.getLogger(CompositeCategorizerEquality.class);

    @Override
	public Logger getLogger() {
        return logger;
    }

    protected CompositeCategorizerEquality(List<ExtractorAndEquality> extractorsAndEqualities) {
        super(extractorsAndEqualities);
    }

    public static class Builder<T> {
        private List<ExtractorAndEquality> eaes = new ArrayList<>();

        public <V> Builder<T> addComponent(Function<T, V> extractor) {
            return addComponent(extractor, new JavaObject<>());
        }

        public <V> Builder<T> addComponent(Function<T, V> extractor, CategorizerEquality<V> equality) {
            ExtractorAndEquality<T, V, Equality<V>> eae = new ExtractorAndEquality<>(extractor, equality);
            eaes.add(eae);
            return this;
        }

        public CompositeCategorizerEquality<T> build() {
            return new CompositeCategorizerEquality<>(eaes);
        }
    }
}
