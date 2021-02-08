package dev.razboy.resonance.server.file;

import dev.razboy.resonance.Resonance;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * Credit MCMastery
 * https://github.com/MCMastery/webbukkit/blob/master/src/com/dgrissom/webbukkit/FileUtils.java
 */

public class FileUtil {
    private static final Path FOLDER = Resonance.websiteFolder;


    public static Path createFile(Path file) {
        if (!Files.exists(file)) {
            try {
                Files.createFile(file);
                return file;
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to create file: " + file.toString());
                return null;
            }
        } else {
            return file;
        }
    }

    public static Path createFolder(Path file) {
        if (!Files.exists(file)) {
            try {
                Files.createDirectories(file);
                return file;
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to create folder: " + file.toString());
                return null;
            }
        }
        return file;
    }
    public static List<String> readFile(Path file) throws IOException {
        if (Files.exists(file)) {
            return Files.readAllLines(file);
        } else {
            return Collections.singletonList("");
        }
    }
    public static Path getFolder() {
        return createFolder(FOLDER);
    }
    public static Path getHome() {
        return createFile(getFolder().resolve("index.html"));
    }
    public static Path get404() {
        return createFile(getFolder().resolve("404.html"));
    }
    public static List<String> getWebsitePage(String request) throws IOException {
        if (request.endsWith("/")) // ends with folder directory
            request += "index.html";
        //System.out.println(request);
        Path file = getFolder().resolve(request.substring(1));
        //System.out.println("1" + getFolder().toFile().getAbsolutePath());
        //System.out.println("2" + file.toFile().getAbsolutePath());
        if (Files.exists(file) && Files.isDirectory(file))
            return getWebsitePage(request + "/index.html");
        if (!Files.exists(file))
            return readFile(get404());
        return readFile(file);
    }
    /**
     * In dev
    public static String getMime(String request) {
        if (request.endsWith("/")) {request += "index.html";}
        Path file = getFolder().resolve(request.substring(1));
        if (Files.exists(file) && Files.isDirectory(file)) {
            return getMime(request + "/index.html");
        }
        if (!Files.exists(file)) {
            return getMime(get404().toFile().getName());
        }
        switch (file.)

    }
     */
}
