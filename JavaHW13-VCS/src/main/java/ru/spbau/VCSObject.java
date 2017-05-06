package ru.spbau;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/** Класс, отвечающий за работу с объектами репозитория.*/
public abstract class VCSObject {

    public static final String BLOB = "blob";
    public static final String TREE = "tree";
    public static final String COMMIT = "commit";
    public static final String BRANCH = "branch";

    protected Repository repository;
    protected byte[] content;
    protected String sha;

    /** Возвращает хеш содержимого объекта.*/
    public @NotNull String getSHA() {
        return sha;
    }

    /** Возвращает содержимое объекта.*/
    public @NotNull byte[] getContent() {
        return content;
    }

    /** Возвращает репозиторий, к которому привязан объект.*/
    public @NotNull Repository getRepository() {
        return repository;
    }

    protected void write() throws IOException {
        FileSystemWorker.writeTo(repository.folders.realObjectsFolder.resolve(sha), content);
    }

}
