package ru.spbau;

import org.jetbrains.annotations.NotNull;

public class TableViewItem {

    private final String name;
    private final Boolean isDir;

    public static final TableViewItem BACK_ITEM = new TableViewItem("..", true);

    public TableViewItem(@NotNull String name, boolean isDir) {
        this.name = name;
        this.isDir = isDir;
    }

    public TableViewItem(@NotNull FileWithType fileWithType) {
        name = fileWithType.getName();
        isDir = fileWithType.isDir();
    }

    public @NotNull String getName() {
        return name;
    }

    public @NotNull Boolean isDir() {
        return isDir;
    }

}
