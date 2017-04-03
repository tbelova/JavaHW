package ru.spbau;

import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Blob extends VCSObject {

    private String name;

    public Blob(@NotNull String name, @NotNull Path path, @NotNull Repository repository)
            throws MyExceptions.IsNotFileException, IOException {

        if (!Files.isRegularFile(path)) {
            throw new MyExceptions.IsNotFileException();
        }

        this.repository = repository;
        this.name = name;
        this.content = Format.readByteContent(path);
        updateSHA();
        write();
    }

    public Blob(@NotNull String name, @NotNull byte[] content, @NotNull Repository repository) throws IOException {
        this.content = content;
        this.name = name;
        this.repository = repository;
        updateSHA();
        write();
    }

    @Override
    public @NotNull String getType() {
        return VCSObject.BLOB;
    }

    public @NotNull String getName() {
        return name;
    }

    private void updateSHA() {
        sha = DigestUtils.sha1Hex(content);
    }

}
