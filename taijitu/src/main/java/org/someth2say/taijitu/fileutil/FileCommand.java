package org.someth2say.taijitu.fileutil;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.*;

/**
 * @author Jordi Sola
 */
public abstract class FileCommand implements Command {
    private static final Logger logger = Logger.getLogger(FileCommand.class);
    private File file;
    private boolean append = false;

    /**
     * @param folder   Folder where file should be
     * @param fileName Name for the file
     */
    protected FileCommand(final File folder, final String fileName) throws CommandException {
        if (folder == null) {
            throw new CommandException("FolderName property cannot be null.");
        }
        if (logger.isDebugEnabled()) {
            logger.debug((new StringBuilder()).append("Folder: ").append(folder).append(" filename: ").append(fileName).toString());
            logger.debug((new StringBuilder()).append("Write dir is ").append(folder.getPath()).toString());
        }
        if (!folder.exists()) {
            logger.debug("Write dir does not exist, creating...");
            if (!folder.mkdir()) {
                throw new CommandException("Unable to create folder " + folder);

            }
        }
        setFile(new File(folder, (new StringBuilder()).append(fileName).append(".").append(getFileExtension()).toString()));
        if (logger.isDebugEnabled()) {
            logger.debug((new StringBuilder()).append("Creating file ").append(getFile().getPath()).toString());
        }
    }

    @Override
    public void rollback() throws CommandException {
        final File file = getFile();
        if (logger.isDebugEnabled()) {
            logger.debug((new StringBuilder()).append("rolling back error write command: ").append(file).toString());
        }
        if (file.exists()) {
            if (logger.isTraceEnabled()) {
                logger.trace((new StringBuilder()).append("file did exist, deleting ").append(file).toString());
            }
            final boolean delete = file.delete();
            if (!delete && logger.isEnabledFor(Level.ERROR)) {
                throw new CommandException((new StringBuilder()).append("problems deleting file ").append(file).toString());
            }
        }
    }

    @Override
    public void process(final Object payload) throws CommandException {
        final File file = getOrDeleteFile();
        logger.info("Writing " + getFileExtension() + " workbook " + file.getName());
        try (final OutputStream os = getFileOutputStream(file)) {
            process(os, payload);
        } catch (final IOException e) {
            throw new CommandException((new StringBuilder()).append("Unable to write data for ").append(file.getName()).toString(), e);
        }
    }

    abstract public void process(final OutputStream os, final Object payload) throws CommandException;

    private File getOrDeleteFile() throws CommandException {
        final File file = getFile();
        if (file.exists() && !file.delete()) {
            throw new CommandException("Can't delete file " + file.getName());
        }
        return file;
    }


    /**
     * @return the append
     */
    public boolean isAppend() {
        return append;
    }

    /**
     * @param _append the append to set
     */
    public void setAppend(final boolean _append) {
        this.append = _append;
    }

    protected FileOutputStream getFileOutputStream(File file) throws FileNotFoundException {
        return new FileOutputStream(file, isAppend());
    }

    protected abstract String getFileExtension();

    /**
     * @return the file
     */
    public File getFile() {
        return file;
    }

    /**
     * @param _file the file to set
     */
    private void setFile(final File _file) {
        this.file = _file;
    }
}
