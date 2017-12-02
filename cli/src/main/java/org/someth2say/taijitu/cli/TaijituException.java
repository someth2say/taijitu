package org.someth2say.taijitu.cli;

/**
 * @author Jordi Sola
 */
public class TaijituException extends Exception {

    private static final long serialVersionUID = 2012537439169849332L;

    public TaijituException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public TaijituException(final String string) {
        super(string);
    }

}
