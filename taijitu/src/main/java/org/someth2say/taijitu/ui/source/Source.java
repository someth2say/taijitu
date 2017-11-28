package org.someth2say.taijitu.ui.source;

import org.someth2say.taijitu.util.Named;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public interface Source<T> extends Named, AutoCloseable {

    List<FieldDescription<?>> getProvidedFields();

    <V> Function<T,V> getExtractor(FieldDescription<V> fd);

    Stream<T> stream();

    @Override
	default void close() throws ClosingException {
    	stream().close();
    }

    class ClosingException extends Exception {
        public ClosingException() {
            super();
        }

        public ClosingException(String message) {
            super(message);
        }

        public ClosingException(String message, Throwable cause) {
            super(message, cause);
        }

        public ClosingException(Throwable cause) {
            super(cause);
        }

        protected ClosingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }

    Class<T> getTypeParameter();
}
