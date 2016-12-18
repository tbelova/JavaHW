package ru.spbau;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Принимает путь и регулярное выражение.
 * По указанному пути находит все zip-архивы и извлекает все файлы, имена которых удовлетворяют регулярному выражению.
 */
public class Main {
    /**
     * Ожидает на вход два аргумента: путь и регулярное выражение.
     * В папку, соответствующую этому пути, с суффиксом _result разархивирует все архивы в папке.
     * */
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Invalid number of arguments.");
            return;
        }

        Path path;
        Pattern pattern;
        try {
            path = Paths.get(args[0]);
            pattern = Pattern.compile(args[1]);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        unZipPatternMatchingFiles(path, pattern);

    }

    private static void unZipPatternMatchingFiles(Path path, Pattern pattern) throws IOException {
        Files.walk(path).filter(path1 -> Files.isRegularFile(path1))
                .filter(path1 -> path1.toFile().getName().endsWith(".zip"))
                .forEach(path1 -> {
                    try {
                        unZipPatternMatchingFiles(path1.toFile(), pattern);
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                });
    }

    private static void unZipPatternMatchingFiles(File file, Pattern pattern) throws IOException {
        ZipFile zipFile;
        zipFile = new ZipFile(file);

        String folder_name = file.getName().substring(0, file.getName().length() - 4);

        Path path = Paths.get("").resolve(file.getParent().toString() + "_result").resolve(folder_name);
        Files.createDirectories(path);

        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.isDirectory()) {
                Files.createDirectories(path.resolve(entry.getName()));
            } else if (pattern.matcher(entry.getName()).matches()) {
                Files.copy(zipFile.getInputStream(entry), path.resolve(entry.getName()));
            }
        }
    }
}
