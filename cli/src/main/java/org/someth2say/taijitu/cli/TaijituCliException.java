package org.someth2say.taijitu.cli;

/**
 * @author Jordi Sola
 */
public class TaijituCliException extends Exception {

    private static final long serialVersionUID = 2012537439169849332L;

    public TaijituCliException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public TaijituCliException(final String string) {
        super(string);
    }

}
