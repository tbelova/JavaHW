package ru.spbau;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/** Класс, который осуществляет работу с файловой системой.*/
public class FileSystemWorker {

    /** Принимает путь до файла, возвращает все строки в нем.*/
    public static @NotNull List<String> readLines(@NotNull Path path) throws IOException {
        return Files.readAllLines(path);
    }

    /**
     * Принимает путь до файла.
     * Если в нем всего одна строка, возвращает ее.
     * Если нет, бросает исключение.
     */
    public static @NotNull String readSingleLine(@NotNull Path path)
            throws MyExceptions.UnknownProblem, IOException {
        List<String> lines = readLines(path);
        if (lines.size() != 1) {
            throw new MyExceptions.UnknownProblem();
        }
        return lines.get(0);
    }

    /** Принимает путь до файла. Возвращает его содержимое.*/
    public static @NotNull byte[] readByteContent(@NotNull Path path) throws IOException {
        return Files.readAllBytes(path);
    }

    /** Принимает путь до файла и то, что нужно в него записать, и записывает.*/
    public static void writeTo(@NotNull Path path, @NotNull byte[] content) throws IOException {
        OutputStream outputStream = Files.newOutputStream(path);
        outputStream.write(content);
        outputStream.close();
    }

    /** Принимает путь до файла и то, что нужно в него записать, и записывает.*/
    public static void writeTo(@NotNull Path path, @NotNull String content) throws IOException {
        writeTo(path, content.getBytes());
    }

    /** Принимает VCSFolders. Создает все необходимые для репозитория файлы и папки.*/
    public static void createRepository(@NotNull VCSFolders folders)
            throws IOException, MyExceptions.IsNotDirectoryException, MyExceptions.AlreadyExistsException {
        if (!Files.isDirectory(folders.repositoryPath)) {
            throw new MyExceptions.IsNotDirectoryException();
        }

        if (Files.exists(folders.realVcsFolder)) {
            if (Files.walk(folders.realVcsFolder).toArray().length != 3) {
                throw new MyExceptions.AlreadyExistsException();
            }
        } else {
            Files.createDirectory(folders.realVcsFolder);
        }
        Files.createFile(folders.realHEADFile);
        Files.createFile(folders.realIndexFile);
        Files.createDirectory(folders.realObjectsFolder);
        Files.createDirectory(folders.realRefsFolder);
        Files.createDirectory(folders.realBranchesFolder);
    }

    /** Принимает VCSFolders. Возвращает true, если существуют все необходимые файлы и папки, и false, иначе.*/
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

    /** Принимает путь до репозитория. Удаляет всю папку с репозиторием.*/
    public static void deleteRepository(@NotNull Path path)
            throws MyExceptions.IsNotDirectoryException, MyExceptions.NotFoundException, IOException {
        deleteRepository(new VCSFolders(path));
    }

    /** Принимает путь до директории, возвращает true, если она существует, и false, иначе.*/
    public static boolean exists(@NotNull Path path) {
        return Files.exists(path);
    }

    /** Принимает путь до директории и удаляет ее.*/
    public static void delete(@NotNull Path path) throws IOException {
        if (Files.isDirectory(path)) {
            FileUtils.deleteDirectory(path.toFile());
        } else {
            Files.deleteIfExists(path);
        }
    }

    /** Принимает путь до директории, возвращает список всех файлов, содержащихся в ней.*/
    public static @NotNull List<Path> walk(@NotNull Path path) throws IOException {
        return Files.walk(path).collect(Collectors.toList());
    }

    /** Принимает путь и создает по этому пути новый файл, если он не существовал.*/
    public static void createFile(@NotNull Path path) throws IOException {
        Files.createDirectories(path.getParent());
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
    }

    /** Принимает путь до файла, поверяет, что это действительно файл.*/
    public static boolean isFile(@NotNull Path path) {
        return Files.isRegularFile(path);
    }

    /**
     * Принимает репозиторий и путь до директории.
     * Проверяет, что эта директория лежит в папке с репозиторием.
     */
    public static boolean inRepository(@NotNull Repository repository, @NotNull Path path) {
        return path.startsWith(repository.folders.repositoryPath) &&
                !path.startsWith(repository.folders.realVcsFolder);
    }

    private static void deleteRepository(@NotNull VCSFolders folders)
            throws MyExceptions.IsNotDirectoryException, MyExceptions.NotFoundException, IOException {
        if (!Files.isDirectory(folders.repositoryPath)) {
            throw new MyExceptions.IsNotDirectoryException();
        }
        if (!Files.exists(folders.realVcsFolder)) {
            throw new MyExceptions.NotFoundException();
        }
        FileUtils.deleteDirectory(folders.repositoryPath.toFile());
    }

}
