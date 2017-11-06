package org.someth2say.taijitu.source;

import org.someth2say.taijitu.config.interfaces.ISourceCfg;
import org.someth2say.taijitu.tuple.ComparableTuple;
import org.someth2say.taijitu.tuple.FieldDescription;
import org.someth2say.taijitu.util.Named;

import java.util.Iterator;
import java.util.List;
//TODO: Move source to be an Stream<T>
public interface Source<T> extends Named, AutoCloseable {

    List<FieldDescription> getFieldDescriptions();

    Iterator<T> iterator();

    ISourceCfg getConfig();

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
