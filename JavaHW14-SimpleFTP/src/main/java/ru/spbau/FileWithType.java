package ru.spbau;

public class FileWithType {

    private String name;

    private boolean is_dir;

    public FileWithType(String name, boolean is_dir) {
        this.name = name;
        this.is_dir = is_dir;
    }

    public String getName() {
        return name;
    }

    public boolean IsDir() {
        return is_dir;
    }

}
