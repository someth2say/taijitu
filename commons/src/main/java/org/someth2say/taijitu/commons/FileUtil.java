package org.someth2say.taijitu.commons;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystemNotFoundException;

/**
 * Created by Jordi Sola on 09/02/2017.
 */
public class FileUtil {

    private FileUtil() {
    }

    /**
     * Dumps to System.out a resource file.
     * Resource files should be present in runtime classpath.
     *
     * @param fileName
     */
    public static void dumpResource(String fileName) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(fileName)))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException | FileSystemNotFoundException e) {
            System.err.println("Unable to read " + fileName + " file! " + e.getMessage());
        }
    }
}
