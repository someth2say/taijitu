package org.someth2say.taijitu.query.properties;

/**
 * @author Jordi Sola
 */
public class HPropertiesException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 5652238016113288259L;

	public HPropertiesException() {
        super();
    }

    public HPropertiesException(String s) {
        super(s);
    }

    public HPropertiesException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public HPropertiesException(Throwable throwable) {
        super(throwable);
    }

    protected HPropertiesException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
