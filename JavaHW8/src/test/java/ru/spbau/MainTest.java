package ru.spbau;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class MainTest {
    private Path main_path = Paths.get("zipTest");
    private Path expected = main_path.resolve("expected");
    private Path test = main_path.resolve("test_folder");
    private Path found = main_path.resolve("test_folder_result");

    @Test
    public void mainTest() throws Exception {
        Main.main(new String [] {test.toString(), ".*test[0|1|2|3]*"});

        equalFilesCheck();
    }

    @After
    public void clean() throws IOException {
        FileUtils.deleteDirectory(Paths.get("zipTest").resolve("test_folder_result").toFile());
    }

    private void equalFilesCheck() throws IOException {
        Set<Path> expectedFiles = Files.walk(expected).map(path -> path.relativize(expected)).collect(Collectors.toSet());
        Set<Path> foundFiles = Files.walk(found).map(path -> path.relativize(found)).collect(Collectors.toSet());
        assertEquals(expectedFiles, foundFiles);

        for (Path path: expectedFiles) {
            File expectedFile = expected.resolve(path).toFile();
            File foundFile = found.resolve(path).toFile();

            if (!expectedFile.isDirectory()) {
                assertFalse(foundFile.isDirectory());
                assertTrue(FileUtils.contentEquals(expectedFile, foundFile));
            } else {
                assertTrue(foundFile.isDirectory());
            }
        }
    }

}