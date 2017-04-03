package ru.spbau;

import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class Blob extends VCSObject {

    private String name;

    public static  @Nullable Blob find(@NotNull String name, @NotNull String hash, @NotNull Path objects)
            throws IOException {
        Stream<Path> pathStream = Files.walk(objects);
        return pathStream.reduce(null, (Blob blob, Path path) -> {
            if (path.getFileName().toString().equals(hash)) {
                Blob resultBlob = null;
                try {
                    resultBlob = Blob.read(name, path, objects);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return resultBlob;
            } else {
                return blob;
            }
        }, (Blob blob1, Blob blob2) -> {
            if (blob1 == null) {
                return blob2;
            } else {
                return blob1;
            }
        });
    }

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
