package org.someth2say.taijitu.fileutil;

/**
 * @author Jordi Sola
 */
public interface Command {

    void process(final Object payload) throws CommandException;

    void rollback() throws CommandException;

}
