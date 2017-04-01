package ru.spbau;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;

public abstract class VCSObject {

    public static final String BLOB = "blob";
    public static final String TREE = "tree";
    public static final String COMMIT = "commit";
    public static final String BRANCH = "branch";
    public static final String TAG = "tag";

    protected Path objects;
    protected byte[] content;
    protected String sha;

    public @NotNull String getSHA() {
        return sha;
    }

    public @NotNull byte[] getContent() {
        return content;
    }

    public abstract @NotNull String getType();

    protected void write() throws IOException {
        Format.writeTo(objects.resolve(sha), content);
    }


}
