package ru.spbau;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public class Head {

    private final Repository repository;

    public Head(@NotNull Repository repository) {
        this.repository = repository;
    }

    public @NotNull Commit getCommit() throws IOException, MyExceptions.UnknownProblem, MyExceptions.IsNotFileException {
        List<String> lines = Format.readLines(repository.folders.realHEADFile);
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
        Commit commit = new Commit(repository.folders.realObjectsFolder.resolve(stringList[1]),
                repository);
        if (commit == null) {
            throw new MyExceptions.UnknownProblem();
        }
        return commit;
    }

    public @NotNull Branch getBranch() throws IOException, MyExceptions.UnknownProblem {
        List<String> lines = Format.readLines(repository.folders.realHEADFile);
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
        Branch branch = Branch.find(stringList[1], repository.branches);
        if (branch == null) {
            throw new MyExceptions.UnknownProblem();
        }
        return branch;
    }

    public @NotNull String getType() throws IOException, MyExceptions.UnknownProblem {
        String[] stringList = Format.readSingleLine(repository.folders.realHEADFile).split(" ");
        if (stringList.length != 2) {
            throw new MyExceptions.UnknownProblem();
        }
        return stringList[0];
    }

    public @NotNull Commit getCommitAnyway() throws IOException, MyExceptions.UnknownProblem, MyExceptions.IsNotFileException {
        if (getType().equals(VCSObject.BRANCH)) {
            return getBranch().getCommit();
        } else {
            return getCommit();
        }
    }

    public @NotNull Tree getTree() throws IOException, MyExceptions.UnknownProblem, MyExceptions.IsNotFileException {
        return getCommitAnyway().getTree();
    }

    public void write(@NotNull Branch branch) throws IOException {
        Format.writeTo(repository.folders.realHEADFile, (VCSObject.BRANCH + " " + branch.getName()));
    }

    public void write(@NotNull Commit commit) throws IOException {
        Format.writeTo(repository.folders.realHEADFile, (VCSObject.COMMIT + " " + commit.getSHA()));
    }


}
