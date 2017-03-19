package ru.spbau;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

public class RepositoryTest {
    private Path repositoryPath = Paths.get("JavaTest");

    @Before
    public void initRepository() throws Exception {
        Files.createDirectory(repositoryPath);
        Repository.initRepository(repositoryPath);
    }

    @Test
    public void getRepositoryTest() throws Exception {
        Repository.getRepository(repositoryPath);
    }

    @Test
    public void addTest() throws Exception {
        Repository repository = Repository.getRepository(repositoryPath);
        if (!Files.exists(repositoryPath.resolve("dir1"))) {
            Files.createDirectory(repositoryPath.resolve("dir1"));
        }
        if (!Files.exists(repositoryPath.resolve("dir1/file1"))) {
            Files.createFile(repositoryPath.resolve("dir1/file1"));
        }
        if (!Files.exists(repositoryPath.resolve("dir1/file2"))) {
            Files.createFile(repositoryPath.resolve("dir1/file2"));
        }
        OutputStream outputStream;
        outputStream = Files.newOutputStream(repositoryPath.resolve("dir1/file1"));
        outputStream.write(("yay").getBytes());
        outputStream.close();
        outputStream = Files.newOutputStream(repositoryPath.resolve("dir1/file2"));
        outputStream.write(("heeey! \n lalala").getBytes());
        outputStream.close();
        repository.add(repositoryPath.resolve("dir1/file1"));
        repository.add(repositoryPath.resolve("dir1/file2"));
    }

    @Test
    public void simpleWorkWithBranchesTest() throws Exception {
        Repository repository = Repository.getRepository(repositoryPath);
        repository.branch("super_branch");
        repository.checkout("super_branch");
        repository.removeBranch("master");
    }

    @Test
    public void commitTest() throws Exception {
        Repository repository = Repository.getRepository(repositoryPath);
        repository.commit("First commit!");
    }

    @Test
    public void logTest() throws Exception {
        Repository repository = Repository.getRepository(repositoryPath);
        repository.commit("First commit!");
        List<CommitWithMessage> commitWithMessageList = repository.log();
        for (CommitWithMessage commitWithMessage: commitWithMessageList) {
            System.out.println(commitWithMessage.getCommit() + " " + commitWithMessage.getMessage());
        }
    }

    @After
    public void removeRepositoryTest() throws Exception {
        Repository.removeRepository(repositoryPath);
    }


}