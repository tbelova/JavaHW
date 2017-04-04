package ru.spbau;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public class Index {

    private Repository repository;

    public Index(@NotNull Repository repository) {
        this.repository = repository;
    }

    public void update(@NotNull Commit commit) throws IOException {
        List<PathWithSHA> pathsWithSHA = commit.getPathWithSHAList();
        String content = "";
        for (PathWithSHA line: pathsWithSHA) {
            content += (line.getPath() + " " + line.getSHA() + "\n");
        }
        FileSystemWorker.writeTo(repository.folders.realIndexFile, content);
    }


}
