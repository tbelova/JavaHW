package ru.spbau;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class FileSystemWorker {

    public static @NotNull List<String> readLines(@NotNull Path path) throws IOException {
        return Files.readAllLines(path);
    }

    public static @NotNull String readSingleLine(@NotNull Path path)
            throws MyExceptions.UnknownProblem, IOException {
        List<String> lines = readLines(path);
        if (lines.size() != 1) {
            throw new MyExceptions.UnknownProblem();
        }
        return lines.get(0);
    }

    public static @NotNull byte[] readByteContent(@NotNull Path path) throws IOException {
        return Files.readAllBytes(path);
    }

    public static void writeTo(@NotNull Path path, @NotNull byte[] content) throws IOException {
        OutputStream outputStream = Files.newOutputStream(path);
        outputStream.write(content);
        outputStream.close();
    }

    public static void writeTo(@NotNull Path path, @NotNull String content) throws IOException {
        writeTo(path, content.getBytes());
    }

    public static void createRepository(@NotNull VCSFolders folders)
            throws IOException, MyExceptions.IsNotDirectoryException, MyExceptions.AlreadyExistsException {
        if (!Files.isDirectory(folders.repositoryPath)) {
            throw new MyExceptions.IsNotDirectoryException();
        }

        if (Files.exists(folders.realVcsFolder)) {
            throw new MyExceptions.AlreadyExistsException();
        }

        Files.createDirectory(folders.realVcsFolder);
        Files.createFile(folders.realHEADFile);
        Files.createFile(folders.realIndexFile);
        Files.createDirectory(folders.realObjectsFolder);
        Files.createDirectory(folders.realRefsFolder);
        Files.createDirectory(folders.realBranchesFolder);
    }

    public static boolean exists(@NotNull VCSFolders folders) {
        if (!Files.isDirectory(folders.repositoryPath) || Files.notExists(folders.realVcsFolder)
                || !Files.isDirectory(folders.realVcsFolder)
                || Files.notExists(folders.realHEADFile)
                || !Files.isRegularFile(folders.realHEADFile)
                || Files.notExists(folders.realIndexFile)
                || !Files.isRegularFile(folders.realIndexFile)
                || Files.notExists(folders.realObjectsFolder)
                || !Files.isDirectory(folders.realObjectsFolder)
                || Files.notExists(folders.realRefsFolder)
                || !Files.isDirectory(folders.realRefsFolder)
                || !Files.isDirectory(folders.realBranchesFolder)) {
            return false;
        }
        return true;
    }

    public static void deleteRepository(@NotNull VCSFolders folders)
            throws MyExceptions.IsNotDirectoryException, MyExceptions.NotFoundException, IOException {
        if (!Files.isDirectory(folders.repositoryPath)) {
            throw new MyExceptions.IsNotDirectoryException();
        }
        if (!Files.exists(folders.realVcsFolder)) {
            throw new MyExceptions.NotFoundException();
        }
        FileUtils.deleteDirectory(folders.repositoryPath.toFile());
    }

    public static void deleteRepository(@NotNull Path path)
            throws MyExceptions.IsNotDirectoryException, MyExceptions.NotFoundException, IOException {
        deleteRepository(new VCSFolders(path));
    }

    public static boolean exists(@NotNull Path path) {
        return Files.exists(path);
    }

    public static void delete(@NotNull Path path) throws IOException {
        if (Files.isDirectory(path)) {
            FileUtils.deleteDirectory(path.toFile());
        } else {
            Files.deleteIfExists(path);
        }
    }

    public static @NotNull List<Path> walk(@NotNull Path path) throws IOException {
        return Files.walk(path).collect(Collectors.toList());
    }

    public static void createFile(@NotNull Path path) throws IOException {
        Files.createDirectories(path.getParent());
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
    }

    public static boolean isFile(@NotNull Path path) {
        return Files.isRegularFile(path);
    }

}
