package ru.spbau;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Класс, хранящий путь до файла и его тип:
 * измененный/удаленный/недобавленный/staged
 */
public class File {

    static final int CHANGED = 0;
    static final int DELETED = 1;
    static final int UNTRACKED = 2;
    static final int STAGED = 3;

    private Path path;
    private int type;

    /** Принимает путь до файла и типа файла, конструирует File.*/
    public File(@NotNull Path path, int type) throws IOException {
        this.path = path;
        this.type = type;
    }

    /** Возвращает путь до файла.*/
    public @NotNull Path getPath() {
        return path;
    }

    /** Возвращает тип файла.*/
    public int getType() {
        return type;
    }


}
