package ru.spbau;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class Branch {

    private String name;
    private Commit commit;
    private Path branches;

    public Branch(@NotNull String name, @NotNull Commit commit, @NotNull Path branches) throws IOException {
        this.name = name;
        this.commit = commit;
        this.branches = branches;
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
        Format.writeTo(branches.resolve(name), commit.getSHA());
    }

}
