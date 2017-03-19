package ru.spbau;

public abstract class VCSObject {

    public static final String BLOB = "blob";
    public static final String TREE = "tree";
    public static final String COMMIT = "commit";
    public static final String BRANCH = "branch";
    public static final String TAG = "tag";

    public abstract String getSHA();

    public abstract String getType();

    public abstract byte[] getContent();

}
