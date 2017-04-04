package ru.spbau;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Commit extends VCSObject implements Comparable<Commit> {

    private Tree tree;
    private String message;
    private List<Commit> parentCommits;
    private Date date;
    private String author;

    public Commit(@NotNull Path path, @NotNull Repository repository)
            throws IOException, MyExceptions.UnknownProblem, MyExceptions.IsNotFileException {
        List<String> lines = FileSystemWorker.readLines(path);
        List<Commit> parents = new ArrayList<>();
        for (int i = 4; i < lines.size(); i++) {
            parents.add(new Commit(repository.folders.realObjectsFolder.resolve(lines.get(i)), repository));
        }
        this.message = lines.get(0);
        this.tree = Tree.read("root", repository.folders.realObjectsFolder.resolve(lines.get(1)), repository);
        this.date = Format.readDate(lines.get(2));
        this.author = lines.get(3);
        this.parentCommits = parents;
        this.repository =repository;
        updateSHA();
        write();
    }

    public Commit(@NotNull String message, @NotNull Tree tree, @NotNull Date date,
                  @NotNull String author, @NotNull List<Commit> parents, @NotNull Repository repository) throws IOException {
        this.message = message;
        this.tree = tree;
        this.date = date;
        this.author = author;
        this.repository = repository;
        parentCommits = new ArrayList<>(parents);
        updateSHA();
        write();
    }

    public Commit(@NotNull String message, @NotNull Tree tree, @NotNull List<Commit> parents, @NotNull Repository repository)
            throws IOException {
        this(message, tree, new Date(), System.getProperty("user.name"), parents, repository);
    }

    public @NotNull List<Commit> log() {
        List<Commit> log = new ArrayList<>();
        log.add(this);
        for (Commit parent: parentCommits) {
            List<Commit> parentLog = parent.log();
            for (Commit commit: parentLog) {
                log.add(commit);
            }
        }
        return log;
    }

    public @NotNull List<PathWithSHA> getPathWithSHAList() {
        return getTree().constructOriginalPaths(repository.folders.repositoryPath);
    }

    public @NotNull String getMessage() {
        return message;
    }

    public @NotNull String getAuthor() {
        return author;
    }

    public @NotNull Tree getTree() {
        return tree;
    }

    public @NotNull Date getDate() {
        return date;
    }

    @Override
    public @NotNull String getType() {
        return VCSObject.COMMIT;
    }

    private void updateSHA() {
        String contentString = message + "\n" + tree.getSHA() + "\n" + Format.writeDate(date) + "\n" + author;
        for (Commit parentCommit: parentCommits) {
            contentString += "\n" + parentCommit.getSHA();
        }
        content = contentString.getBytes();
        sha = Format.getSHAFromByteArray(content);
    }

    @Override
    public int compareTo(@NotNull Commit commit) {
        return date.compareTo(commit.getDate());
    }

}


