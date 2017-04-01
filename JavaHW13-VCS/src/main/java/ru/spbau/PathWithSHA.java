package ru.spbau;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class PathWithSHA {
    private Path path;
    private String sha;

    public PathWithSHA(@NotNull Path path, @NotNull String sha) {
        this.path = path;
        this.sha = sha;
    }

    public @NotNull Path getPath() {
        return path;
    }

    public @NotNull String getSHA() {
        return sha;
    }

}
