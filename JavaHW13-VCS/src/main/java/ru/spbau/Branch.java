package ru.spbau;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class Branch {

    private final Repository repository;

    private String name;
    private Commit commit;

    public Branch(@NotNull String name, @NotNull Commit commit, @NotNull Repository repository) throws IOException {
        this.name = name;
        this.commit = commit;
        this.repository = repository;
        write();
    }

    public static @Nullable Branch find(@NotNull String name, @NotNull List<Branch> branches) {
        Branch branchFound = null;
        for (Branch branch: branches) {
            if (branch.getName().equals(name)) {
                branchFound = branch;
            }
        }
        return branchFound;
    }

    public @NotNull String getName() {
        return name;
    }

    public @NotNull Commit getCommit() {
        return commit;
    }

    public void setCommit(@NotNull Commit commit) throws IOException {
        this.commit = commit;
        write();
    }

    private void write() throws IOException {
        Format.writeTo(repository.folders.realBranchesFolder.resolve(name), commit.getSHA());
    }

    public Branch(@NotNull Path path, @NotNull Repository repository)
            throws IOException, MyExceptions.UnknownProblem, MyExceptions.IsNotFileException {
        List<String> lines = Format.readLines(path);
        Commit commit = new Commit(repository.folders.realObjectsFolder.resolve(lines.get(0)), repository);
        if (commit != null) {
            this.name = path.getFileName().toString();
            this.commit = commit;
            this.repository = repository;
        } else {
            throw new MyExceptions.UnknownProblem();
        }
    }


}
