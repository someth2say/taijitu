
package org.someth2say.taijitu.fileutil;

/**
 * @author Jordi Sola
 */
public class CommandException extends Exception {

	private static final long serialVersionUID = 7603961798083770358L;

	public CommandException(final String arg0) {
		super(arg0);
	}

	public CommandException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}

}
