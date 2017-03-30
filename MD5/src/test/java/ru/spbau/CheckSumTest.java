package ru.spbau;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CheckSumTest {
    @Test
    public void simpleSingleThreadCheckSumTest() throws Exception {
        Path path = Paths.get("directory");
        Path file = path.resolve("file");

        Files.createDirectory(path);
        Files.createFile(file);

        OutputStream outputStream;
        outputStream = Files.newOutputStream(file);
        outputStream.write("Yay".getBytes());
        outputStream.close();

        assertTrue(Arrays.equals(SingleThreadCheckSum.checkSum(path), SingleThreadCheckSum.checkSum(path)));
        assertTrue(Arrays.equals(SingleThreadCheckSum.checkSum(file), SingleThreadCheckSum.checkSum(file)));

        FileUtils.deleteDirectory(path.toFile());
    }

    @Test
    public void simpleForkJoinCheckSumTest() throws Exception {
        Path path = Paths.get("directory");
        Path file = path.resolve("file");

        Files.createDirectory(path);
        Files.createFile(file);

        OutputStream outputStream;
        outputStream = Files.newOutputStream(file);
        outputStream.write("Yay".getBytes());
        outputStream.close();

        assertTrue(Arrays.equals(ForkJoinCheckSum.checkSum(path), SingleThreadCheckSum.checkSum(path)));
        assertTrue(Arrays.equals(ForkJoinCheckSum.checkSum(file), SingleThreadCheckSum.checkSum(file)));

        FileUtils.deleteDirectory(path.toFile());
    }

    @Test
    public void simpleCheckSumTest() throws Exception {
        Path path = Paths.get("directory");
        Path file = path.resolve("file");

        Files.createDirectory(path);
        Files.createFile(file);

        OutputStream outputStream;
        outputStream = Files.newOutputStream(file);
        outputStream.write("Yay".getBytes());
        outputStream.close();

        assertTrue(Arrays.equals(SingleThreadCheckSum.checkSum(path), ForkJoinCheckSum.checkSum(path)));
        assertTrue(Arrays.equals(SingleThreadCheckSum.checkSum(file), ForkJoinCheckSum.checkSum(file)));

        FileUtils.deleteDirectory(path.toFile());
    }

    @Test
    public void checkSumTest() throws Exception {
        Path path = Paths.get("directory");
        Files.createDirectory(path);

        createBigFolder(path, 4, 5);

        assertTrue(Arrays.equals(SingleThreadCheckSum.checkSum(path), ForkJoinCheckSum.checkSum(path)));
        assertFalse(Arrays.equals(SingleThreadCheckSum.checkSum(path), ForkJoinCheckSum.checkSum(path.resolve("file1"))));

        FileUtils.deleteDirectory(path.toFile());
    }

    private void createBigFolder(Path path, int numberOfFolders, int numberOfFiles) throws IOException {

        for (int i = 0; i < numberOfFiles; i++) {
            Path file = path.resolve("file" + i);
            Files.createFile(file);
        }

        for (int i = 0; i < numberOfFolders; i++) {
            Path folder = path.resolve("folder" + i);
            Files.createDirectory(folder);
            createBigFolder(folder, numberOfFolders - 1, numberOfFiles);
        }

    }


}