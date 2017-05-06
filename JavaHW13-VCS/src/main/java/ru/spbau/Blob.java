package ru.spbau;

import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Класс, отвечающий за файл, хранящийся в репозитории.*/
public class Blob extends VCSObject {

    private String name;

    /** Принимает имя файла, путь до его копии в репозитории и репозиторий и конструирует Blob.*/
    public Blob(@NotNull String name, @NotNull Path path, @NotNull Repository repository)
            throws MyExceptions.IsNotFileException, IOException {

        if (!Files.isRegularFile(path)) {
            throw new MyExceptions.IsNotFileException();
        }

        this.repository = repository;
        this.name = name;
        this.content = FileSystemWorker.readByteContent(path);
        updateSHA();
    }

    /**
     * Принимает имя файла, его содержимое и репозиторий.
     * Создает соответствующий файл в репозитории и конструирует Blob.
     */
    public Blob(@NotNull String name, @NotNull byte[] content, @NotNull Repository repository) throws IOException {
        this.content = content;
        this.name = name;
        this.repository = repository;
        updateSHA();
        write();
    }

    /** Возвращает имя файла, соответствующего этому Blob-у.*/
    public @NotNull String getName() {
        return name;
    }

    private void updateSHA() {
        sha = DigestUtils.sha1Hex(content);
    }

}
