package ru.spbau;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
    public void simpleAddTest() throws Exception {

        Repository repository = Repository.getRepository(repositoryPath);

        Files.createDirectory(repositoryPath.resolve("dir1"));
        Files.createFile(repositoryPath.resolve("dir1/file1"));
        Files.createFile(repositoryPath.resolve("dir1/file2"));

        repository.add(Paths.get("dir1/file1"));
        repository.add(Paths.get("dir1/file2"));

        assertTrue(Files.exists(repositoryPath.resolve("dir1/file1")));
        assertTrue(Files.exists(repositoryPath.resolve("dir1/file2")));

    }

    @Test
    public void simpleWorkWithBranchesTest() throws Exception {

        Repository repository = Repository.getRepository(repositoryPath);

        assertEquals("master", repository.getCurrentBranch());

        repository.branch("super_branch");
        repository.checkout("super_branch");

        assertEquals("super_branch", repository.getCurrentBranch());

        repository.removeBranch("master");
        repository.createBranchAndCheckout("super_master");

        assertEquals("super_master", repository.getCurrentBranch());

    }

    @Test
    public void simpleCommitTest() throws Exception {
        Repository repository = Repository.getRepository(repositoryPath);
        repository.commit("First commit!");
    }

    @Test
    public void simpleAddAndCommitTest() throws Exception {

        Repository repository = Repository.getRepository(repositoryPath);

        Files.createDirectory(repositoryPath.resolve("dir1"));
        Files.createFile(repositoryPath.resolve("dir1/file1"));
        Files.createFile(repositoryPath.resolve("dir1/file2"));

        repository.add(Paths.get("dir1/file1"));
        repository.add(Paths.get("dir1/file2"));

        repository.commit("First commit!");

    }

    @Test
    public void workWithBranchesTest() throws Exception {

        Repository repository = Repository.getRepository(repositoryPath);

        assertFalse(Files.exists(repositoryPath.resolve("dir1/file1")));

        repository.createBranchAndCheckout("new_branch");

        Files.createDirectory(repositoryPath.resolve("dir1"));
        Files.createFile(repositoryPath.resolve("dir1/file1"));

        repository.add(Paths.get("dir1/file1"));

        assertTrue(Files.exists(repositoryPath.resolve("dir1/file1")));

        repository.commit("First commit!");

        repository.checkout("master");

        assertFalse(Files.exists(repositoryPath.resolve("dir1/file1")));

        repository.checkout("new_branch");

        assertTrue(Files.exists(repositoryPath.resolve("dir1/file1")));

    }

    @Test
    public void simpleLogTest() throws Exception {

        Repository repository = Repository.getRepository(repositoryPath);
        repository.commit("First commit!");
        repository.commit("Second commit!");
        List<LogMessage> logMessageList = repository.log();
        assertEquals(3, logMessageList.size());
        assertEquals("Initial commit", logMessageList.get(0).getMessage());
        assertEquals("First commit!", logMessageList.get(1).getMessage());
        assertEquals("Second commit!", logMessageList.get(2).getMessage());

    }

    @Test
    public void logTest() throws Exception {

        Repository repository = Repository.getRepository(repositoryPath);

        assertEquals(1, repository.log().size());

        repository.commit("commit to master-2");

        assertEquals(2, repository.log().size());

        repository.createBranchAndCheckout("new_branch");

        assertEquals(2, repository.log().size());

        repository.commit("commit to new branch-1");

        assertEquals(3, repository.log().size());

        repository.checkout("master");
        assertEquals(2, repository.log().size());
    }

    @Test
    public void simpleMergeTest() throws Exception {

        Repository repository = Repository.getRepository(repositoryPath);

        repository.createBranchAndCheckout("first");

        repository.commit("first-1");
        assertEquals(2, repository.log().size());

        repository.checkout("master");
        assertEquals(1, repository.log().size());

        repository.merge("first");
        assertEquals(3, repository.log().size());
    }

    @Test
    public void mergeTest() throws Exception {

        Repository repository = Repository.getRepository(repositoryPath);

        repository.createBranchAndCheckout("first");

        Files.createDirectory(repositoryPath.resolve("dir1"));

        Files.createFile(repositoryPath.resolve("dir1/file1"));

        repository.add(Paths.get("dir1/file1"));
        repository.commit("First commit");

        repository.checkout("master");
        repository.createBranchAndCheckout("second");

        Files.createDirectory(repositoryPath.resolve("dir1"));
        Files.createFile(repositoryPath.resolve("dir1/file2"));

        repository.add(Paths.get("dir1/file2"));
        repository.commit("Second commit");

        repository.checkout("master");

        assertFalse(Files.exists(repositoryPath.resolve("dir1/file1")));
        assertFalse(Files.exists(repositoryPath.resolve("dir1/file2")));

        repository.merge("first");

        assertTrue(Files.exists(repositoryPath.resolve("dir1/file1")));
        assertFalse(Files.exists(repositoryPath.resolve("dir1/file2")));

        repository.branch("third");
        repository.checkout("third");

        Files.createDirectory(repositoryPath.resolve("dir2"));
        Files.createFile(repositoryPath.resolve("dir2/file1"));

        repository.add(Paths.get("dir2/file1"));
        repository.commit("Third commit");
        repository.merge("second");

        assertTrue(Files.exists(repositoryPath.resolve("dir1/file1")));
        assertTrue(Files.exists(repositoryPath.resolve("dir1/file2")));
        assertTrue(Files.exists(repositoryPath.resolve("dir2/file1")));

        repository.checkout("master");
        repository.merge("second");

        assertTrue(Files.exists(repositoryPath.resolve("dir1/file1")));
        assertTrue(Files.exists(repositoryPath.resolve("dir1/file2")));
        assertFalse(Files.exists(repositoryPath.resolve("dir2/file1")));

        repository.merge("third");
        assertTrue(Files.exists(repositoryPath.resolve("dir1/file1")));
        assertTrue(Files.exists(repositoryPath.resolve("dir1/file2")));
        assertTrue(Files.exists(repositoryPath.resolve("dir2/file1")));

    }

    @Test
    public void simpleStatusTest() throws Exception {

        Repository repository = Repository.getRepository(repositoryPath);

        List<File> files = repository.status();
        assertEquals(0, files.size());

        Files.createFile(repositoryPath.resolve("a"));

        files = repository.status();
        assertEquals(1, files.size());
        assertEquals(File.UNTRACKED, files.get(0).getType());

        repository.add(Paths.get("a"));

        files = repository.status();
        assertEquals(1, files.size());
        assertEquals(File.STAGED, files.get(0).getType());

        FileSystemWorker.writeTo(repositoryPath.resolve("a"), "yay");

        files = repository.status();
        assertEquals(1, files.size());
        assertEquals(File.CHANGED, files.get(0).getType());
    }

    @Test
    public void simpleResetToStagedTest() throws Exception {

        Repository repository = Repository.getRepository(repositoryPath);

        Files.createFile(repositoryPath.resolve("a"));

        List<File> files = repository.status();
        assertEquals(1, files.size());
        assertEquals(File.UNTRACKED, files.get(0).getType());

        repository.resetToStaged(Paths.get("a"));

        files = repository.status();
        assertEquals(0, files.size());

        Files.createFile(repositoryPath.resolve("b"));

        files = repository.status();
        assertEquals(1, files.size());
        assertEquals(File.UNTRACKED, files.get(0).getType());

        repository.add(Paths.get("b"));

        files = repository.status();
        assertEquals(1, files.size());
        assertEquals(File.STAGED, files.get(0).getType());

        FileSystemWorker.writeTo(repositoryPath.resolve("b"), "bb");

        assertEquals(1, FileSystemWorker.readLines(repositoryPath.resolve("b")).size());

        files = repository.status();
        assertEquals(1, files.size());
        assertEquals(File.CHANGED, files.get(0).getType());

        repository.resetToStaged(Paths.get("b"));

        files = repository.status();
        assertEquals(1, files.size());
        assertEquals(File.STAGED, files.get(0).getType());

        assertEquals(0, FileSystemWorker.readLines(repositoryPath.resolve("b")).size());

    }

    @Test
    public void simpleResetToCommitTest() throws Exception {

        Repository repository = Repository.getRepository(repositoryPath);

        Files.createFile(repositoryPath.resolve("a"));

        repository.add(Paths.get("a"));

        repository.commit("a");

        FileSystemWorker.writeTo(repositoryPath.resolve("a"), "yay");

        repository.add(Paths.get("a"));

        assertEquals("yay", FileSystemWorker.readSingleLine(repositoryPath.resolve("a")));

        repository.resetToCommit(repositoryPath.resolve("a"));

        assertEquals(0, FileSystemWorker.readByteContent(repositoryPath.resolve("a")).length);

    }

    @Test
    public void simpleRmTest() throws Exception {

        Repository repository = Repository.getRepository(repositoryPath);

        Files.createFile(repositoryPath.resolve("a"));
        assertEquals(1, repository.status().size());

        repository.rm(Paths.get("a"));
        assertEquals(0, repository.status().size());

        Files.createFile(repositoryPath.resolve("b"));
        repository.add(Paths.get("b"));
        assertEquals(1, repository.status().size());

        repository.rm(Paths.get("b"));

        assertEquals(0, repository.status().size());

    }

    @Test
    public void simpleCleanTest() throws Exception {

        Repository repository = Repository.getRepository(repositoryPath);

        Files.createFile(repositoryPath.resolve("a"));
        Files.createFile(repositoryPath.resolve("b"));
        Files.createFile(repositoryPath.resolve("c"));
        Files.createFile(repositoryPath.resolve("d"));

        repository.add(Paths.get("a"));
        repository.add(Paths.get("b"));

        assertEquals(4, repository.status().size());

        repository.clean();

        assertEquals(2, repository.status().size());

    }

    @After
    public void removeRepositoryTest() throws Exception {
        Repository.removeRepository(repositoryPath);
    }

}