package ru.spbau;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
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

    public static @Nullable Commit read(@NotNull Path path, Path objects) {
        try {
            List<String> lines = Files.readAllLines(path);
            List<Commit> parents = new ArrayList<>();
            for (int i = 4; i < lines.size(); i++) {
                parents.add(read(objects.resolve(lines.get(i)), objects));
            }
            return new Commit(lines.get(0),
                    Tree.read("root", objects.resolve(lines.get(1)), objects),
                    Format.readDate(lines.get(2)), lines.get(3), parents, objects);
        } catch (Exception e) {
            return null;
        }
    }

    public Commit(@NotNull String message, @NotNull Tree tree, @NotNull Date date,
                  @NotNull String author, @NotNull List<Commit> parents, Path objects) throws IOException {
        this.message = message;
        this.tree = tree;
        this.date = date;
        this.author = author;
        this.objects = objects;
        parentCommits = new ArrayList<>(parents);
        String contentString = message + "\n" + tree.getSHA() + "\n" + Format.writeDate(date) + "\n" + author;
        for (Commit parentCommit: parentCommits) {
            contentString += "\n" + parentCommit.getSHA();
        }
        content = contentString.getBytes();
        updateSHA();
        write();
    }

    public Commit(@NotNull String message, @NotNull Tree tree, @NotNull List<Commit> parents, Path objects)
            throws IOException {
        this(message, tree, new Date(), System.getProperty("user.name"), parents, objects);
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
        sha = Format.getSHAFromByteArray(content);
    }

    @Override
    public int compareTo(@NotNull Commit commit) {
        return date.compareTo(commit.getDate());
    }
}


