package ru.spbau;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/** Класс, который отвеает за работу с веткой.*/
public class Branch {

    private final Repository repository;

    private String name;
    private Commit commit;

    /**
     * Принимающий имя ветки и коммит, который будет ей соответствовать.
     * Создает ветку в репозитории и конструирует Branch.
     */
    public Branch(@NotNull String name, @NotNull Commit commit) throws IOException {
        this.name = name;
        this.commit = commit;
        this.repository = commit.getRepository();
        write();
    }

    /** Принимает путь до файла, соответствующего ветке, и конструирует Branch.*/
    public Branch(@NotNull Path path, @NotNull Repository repository)
            throws IOException, MyExceptions.UnknownProblem, MyExceptions.IsNotFileException {
        List<String> lines = FileSystemWorker.readLines(path);
        Commit commit = new Commit(repository.folders.realObjectsFolder.resolve(lines.get(0)), repository);
        this.name = path.getFileName().toString();
        this.commit = commit;
        this.repository = repository;
    }

    /** Возвращает имя ветки.*/
    public @NotNull String getName() {
        return name;
    }

    /** Возвращает коммит, соответствующий ветке.*/
    public @NotNull Commit getCommit() {
        return commit;
    }

    /** Переставляет ветку на другой коммит.*/
    public void setCommit(@NotNull Commit commit) throws IOException {
        this.commit = commit;
        write();
    }

    private void write() throws IOException {
        FileSystemWorker.writeTo(repository.folders.realBranchesFolder.resolve(name), commit.getSHA());
    }

}
