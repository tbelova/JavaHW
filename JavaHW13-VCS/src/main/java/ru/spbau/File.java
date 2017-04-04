package ru.spbau;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;

public class File {

    static final int CHANGED = 0;
    static final int DELETED = 0;
    static final int UNTRACKED = 0;
    static final int STAGED = 0;

    private Path path;
    private int type;

    public File(@NotNull Path path, int type) throws IOException {
        this.path = path;
        this.type = type;
    }

    public @NotNull Path getPath() {
        return path;
    }

    public int getType() {
        return type;
    }


}
