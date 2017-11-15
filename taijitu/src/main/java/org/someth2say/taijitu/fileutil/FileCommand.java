package org.someth2say.taijitu.fileutil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @author Jordi Sola
 */
public abstract class FileCommand implements Command {
    private static final Logger logger = LoggerFactory.getLogger(FileCommand.class);
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
        logger.debug("Folder: {} filename: {} ", folder, fileName);
        if (!folder.exists()) {
            logger.debug("Write dir does not exist, creating...");
            if (!folder.mkdir()) {
                throw new CommandException("Unable to create folder " + folder);

            }
        }
        setFile(new File(folder, (new StringBuilder()).append(fileName).append(".").append(getFileExtension()).toString()));
        logger.debug("Creating file {}", getFile().getPath());
    }

    @Override
    public void rollback() throws CommandException {
        final File file = getFile();
        logger.debug("Rolling back error write command: {}", file);
        if (file.exists()) {
            logger.trace("file did exist, deleting {}", file);
            final boolean delete = file.delete();
            if (!delete) {
                throw new CommandException((new StringBuilder()).append("problems deleting file ").append(file).toString());
            }
        }
    }

    @Override
    public void process(final Object payload) throws CommandException {
        final File file = getOrDeleteFile();
        logger.info("Writing {} workbook {}", getFileExtension(), file.getName());
        try (final OutputStream os = getFileOutputStream(file)) {
            process(os, payload);
        } catch (final IOException e) {
            throw new CommandException((new StringBuilder()).append("Unable to write data for ").append(file.getName()).toString(), e);
        }
    }

    protected abstract void process(final OutputStream os, final Object payload) throws CommandException;

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

    private FileOutputStream getFileOutputStream(File file) throws FileNotFoundException {
        return new FileOutputStream(file, isAppend());
    }

    protected abstract String getFileExtension();

    /**
     * @return the file
     */
    protected File getFile() {
        return file;
    }

    /**
     * @param _file the file to set
     */
    private void setFile(final File _file) {
        this.file = _file;
    }
}
