package ru.spbau;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;

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
