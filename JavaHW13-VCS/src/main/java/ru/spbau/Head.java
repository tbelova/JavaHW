package ru.spbau;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

/** Класс, отвечающий за работу с файлом Head, в котором хранится текущая ветка или коммит.*/
public class Head {

    private final Repository repository;

    /** Принимает репозиторий, конструирует Head.*/
    public Head(@NotNull Repository repository) {
        this.repository = repository;
    }

    /**
     * Если в файле Head лежит текущий коммит, возвращает его.
     * Иначе бросает исключение.
     */
    public @NotNull Commit getCommit() throws IOException, MyExceptions.UnknownProblem {
        List<String> lines = FileSystemWorker.readLines(repository.folders.realHEADFile);
        if (lines.size() != 1) {
            throw new MyExceptions.UnknownProblem();
        }
        String[] stringList = lines.get(0).split(" ");
        if (stringList.length != 2) {
            throw new MyExceptions.UnknownProblem();
        }
        if (!stringList[0].equals(VCSObject.COMMIT)) {
            throw new MyExceptions.UnknownProblem();
        }
        Commit commit;
        try {
            commit = new Commit(repository.folders.realObjectsFolder.resolve(stringList[1]), repository);
        } catch (MyExceptions.IsNotFileException e) {
            throw new MyExceptions.UnknownProblem();
        }
        if (commit == null) {
            throw new MyExceptions.UnknownProblem();
        }
        return commit;
    }

    /**
     * Если в файле Head лежит текущая ветка, возвращает ее.
     * Иначе бросает исключение.
     */
    public @NotNull Branch getBranch() throws IOException, MyExceptions.UnknownProblem {
        List<String> lines = FileSystemWorker.readLines(repository.folders.realHEADFile);
        if (lines.size() != 1) {
            throw new MyExceptions.UnknownProblem();
        }
        String[] stringList = lines.get(0).split(" ");
        if (stringList.length != 2) {
            throw new MyExceptions.UnknownProblem();
        }
        if (!stringList[0].equals(VCSObject.BRANCH)) {
            throw new MyExceptions.UnknownProblem();
        }
        Branch branch = repository.findBranch(stringList[1]);
        if (branch == null) {
            throw new MyExceptions.UnknownProblem();
        }
        return branch;
    }

    /**
     * Возвращает VCSObject.BRANCH, если в Head записана текущая ветка,
     * и VCSObject.COMMIT, если в Head записан текущий коммит.
     */
    public @NotNull String getType() throws IOException, MyExceptions.UnknownProblem {
        String[] stringList = FileSystemWorker.readSingleLine(repository.folders.realHEADFile).split(" ");
        if (stringList.length != 2) {
            throw new MyExceptions.UnknownProblem();
        }
        return stringList[0];
    }

    /** Если в Head записан коммит, возвращает его, иначе возвращает коммит, на который указывает текущая ветка.*/
    public @NotNull Commit getCommitAnyway() throws IOException, MyExceptions.UnknownProblem {
        if (getType().equals(VCSObject.BRANCH)) {
            return getBranch().getCommit();
        } else {
            return getCommit();
        }
    }

    /** Возвращает дерево, на которое указывает текущий коммит.*/
    public @NotNull Tree getTree() throws IOException, MyExceptions.UnknownProblem {
        return getCommitAnyway().getTree();
    }

    /** Принимает ветку и записывает ее в Head.*/
    public void write(@NotNull Branch branch) throws IOException {
        FileSystemWorker.writeTo(repository.folders.realHEADFile, (VCSObject.BRANCH + " " + branch.getName()));
    }

    /** Принимает коммит и записывает его в Head.*/
    public void write(@NotNull Commit commit) throws IOException {
        FileSystemWorker.writeTo(repository.folders.realHEADFile, (VCSObject.COMMIT + " " + commit.getSHA()));
    }


}
