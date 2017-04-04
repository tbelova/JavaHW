package ru.spbau;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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

    public @NotNull List<PathWithSHA> getPathsWithSHA() throws MyExceptions.UnknownProblem, IOException {

        List<String> lines = FileSystemWorker.readLines(repository.folders.realIndexFile);
        List<PathWithSHA> pathsWithSHA = new ArrayList<>();

        for (String line: lines) {
            String[] strings = line.split(" ");
            if (strings.length != 2) {
                throw new MyExceptions.UnknownProblem();
            }
            pathsWithSHA.add(new PathWithSHA(Paths.get(strings[0]), strings[1]));
        }

        return pathsWithSHA;
    }

    public @NotNull List<File> getAllFiles() throws IOException, MyExceptions.UnknownProblem {

        List<PathWithSHA> pathWithSHAs = getPathsWithSHA();
        List<File> files = new ArrayList<>();

        for (PathWithSHA pathWithSHA: pathWithSHAs) {
            if (isChanged(pathWithSHA)) {
                files.add(new File(pathWithSHA.getPath(), File.CHANGED));
            } else if (isDeleted(pathWithSHA)) {
                files.add(new File(pathWithSHA.getPath(), File.DELETED));
            } else {
                files.add(new File(pathWithSHA.getPath(), File.STAGED));
            }
        }

        files.addAll(getUntrackedFiles());

        return files;

    }

    public @NotNull List<File> getUntrackedFiles() throws IOException, MyExceptions.UnknownProblem {

        List<PathWithSHA> pathWithSHAs = getPathsWithSHA();
        List<File> files = new ArrayList<>();
        List<Path> allFilesInRepository = FileSystemWorker.walk(repository.folders.repositoryPath);

        for (Path path: allFilesInRepository) {
            if (FileSystemWorker.isFile(path)) {
                boolean found = false;
                for (PathWithSHA pathWithSHA: pathWithSHAs) {
                    if (pathWithSHA.getPath().equals(path)) {
                        found = true;
                    }
                }
                if (!found) {
                    files.add(new File(path, File.UNTRACKED));
                }
            }
        }

        return files;

    }

    public @Nullable String getSHA(@NotNull Path path) throws IOException, MyExceptions.UnknownProblem {

        List<PathWithSHA> pathWithSHAs = getPathsWithSHA();

        for (PathWithSHA pathWithSHA: pathWithSHAs) {
            if (pathWithSHA.getPath().equals(path)) {
                return pathWithSHA.getSHA();
            }
        }

        return null;
    }

    private boolean isChanged(@NotNull PathWithSHA pathWithSHA) throws IOException {
        Path path = pathWithSHA.getPath();
        String sha = pathWithSHA.getSHA();
        return FileSystemWorker.exists(path) &&
                !Format.getSHAFromByteArray(FileSystemWorker.readByteContent(path)).equals(sha);
    }

    private boolean isDeleted(@NotNull PathWithSHA pathWithSHA) {
        return !FileSystemWorker.exists(pathWithSHA.getPath());
    }



}
