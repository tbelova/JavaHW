package ru.spbau;

import org.jetbrains.annotations.NotNull;

/**
 * Класс, хранящий описание файла или папки:
 * -имя
 * -является ли директорией
 */
public class FileWithType {

    private String name;

    private boolean is_dir;

    /** Конструктор от имени и флага.*/
    public FileWithType(@NotNull String name, boolean is_dir) {
        this.name = name;
        this.is_dir = is_dir;
    }

    /** Возвращает имя файла/папки*/
    public @NotNull String getName() {
        return name;
    }

    /** Возвращает true для папки и false иначе.*/
    public boolean IsDir() {
        return is_dir;
    }

}
