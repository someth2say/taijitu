package org.someth2say.taijitu.source;

import org.someth2say.taijitu.tuple.FieldDescription;
import org.someth2say.taijitu.util.Named;

import java.util.List;
import java.util.stream.Stream;

public interface Source<T> extends Named, AutoCloseable {

    //TODO: This is a breack for the SRP!!! Describing the source contents should not be done by the source (stream)
    List<FieldDescription> getProvidedFields();

    Stream<T> stream();

    void close() throws ClosingException;

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
}
