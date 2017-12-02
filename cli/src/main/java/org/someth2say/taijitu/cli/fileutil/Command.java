package org.someth2say.taijitu.cli.fileutil;

/**
 * @author Jordi Sola
 */
interface Command {

    void process(final Object payload) throws CommandException;

    void rollback() throws CommandException;

}
