package org.printing_factory.util;

import java.nio.file.Path;

public class PrintPath {
    Path path;

    private PrintPath(Path path) {
        this.path = path;
    }

    public static PrintPath into(Path absPath, Path dir) {
        return new PrintPath(dir.relativize(absPath));
    }

    public String[] getParts() {
        System.out.println(path.toString());
        return path.toString().split(System.getProperty("os.name").toLowerCase().contains("win") ? "\\\\" : "/");
    }
}