package ru.spbau;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/** Класс, хранящий путь до файла и хеш содержимого этого файла.*/
public class PathWithSHA {
    private final Path path;
    private final String sha;

    /** Принимает путь до файла и хеш содержимого и конструирует PathWithSHA.*/
    public PathWithSHA(@NotNull Path path, @NotNull String sha) {
        this.path = path;
        this.sha = sha;
    }

    /** Возвращает путь до файла.*/
    public @NotNull Path getPath() {
        return path;
    }

    /** Возвращает хеш содержимого файла.*/
    public @NotNull String getSHA() {
        return sha;
    }

}
