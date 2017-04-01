package ru.spbau;

import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Blob extends VCSObject {

    private String name;

    public static  @NotNull Blob read(@NotNull String name, @NotNull Path path, @NotNull Path objects)
            throws MyExceptions.IsNotFileException, IOException {
        if (!Files.isRegularFile(path)) {
            throw new MyExceptions.IsNotFileException();
        }

        return new Blob(name, Files.readAllBytes(path), objects);
    }

    public Blob(@NotNull String name, @NotNull byte[] content, Path objects) throws IOException {
        this.content = content;
        this.name = name;
        this.objects = objects;
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
