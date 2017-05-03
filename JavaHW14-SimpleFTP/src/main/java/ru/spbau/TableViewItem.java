package ru.spbau;

import org.jetbrains.annotations.NotNull;

/**
 * Класс, представляющий из себя элемент таблицы.
 * Содержит информацию о файле или директории:
 * - название
 * - является ли директорией
 */
public class TableViewItem {

    private final String name;
    private final Boolean isDir;

    public static final TableViewItem BACK_ITEM = new TableViewItem("..", true);

    /** Конструктор, копирующий значения переданных аргументов.*/
    public TableViewItem(@NotNull String name, boolean isDir) {
        this.name = name;
        this.isDir = isDir;
    }

    /** Конструирует элемент из объекта типа FileWithType*/
    public TableViewItem(@NotNull FileWithType fileWithType) {
        name = fileWithType.getName();
        isDir = fileWithType.isDir();
    }

    /** Возвращает имя файла/папки.*/
    public @NotNull String getName() {
        return name;
    }

    /** Возвращает true в случае директории и false иначе.*/
    public @NotNull Boolean isDir() {
        return isDir;
    }

}
