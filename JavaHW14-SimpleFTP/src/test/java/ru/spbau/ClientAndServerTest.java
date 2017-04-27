package ru.spbau;

import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


public class ClientAndServerTest {

    private Path path;
    private Server server;
    private Path myPath;

    private static Logger logger = LoggerFactory.getLogger(ClientAndServerTest.class);

    @Before
    public void before() throws Exception {

        logger.debug("in before");

        TemporaryFolder folder = new TemporaryFolder();
        TemporaryFolder myFolder = new TemporaryFolder();

        myFolder.create();

        folder.create();
        folder.newFile("a");
        folder.newFile("b");
        folder.newFolder("c");

        path = folder.getRoot().toPath();
        myPath = myFolder.getRoot().toPath();

        OutputStream outputStream = new FileOutputStream(path.resolve("a").toFile());
        outputStream.write("hey".getBytes());
        outputStream.close();

        outputStream = new FileOutputStream(path.resolve("b").toFile());
        outputStream.write("yay".getBytes());
        outputStream.close();

        server = new Server(path);

        server.start();

    }

    private void connect(@NotNull Client client) throws IOException {

        logger.debug("in connect");

        while (true) {

            try {
                client.connect();
            } catch (ConnectException e) {
                continue;
            }

            return;

        }

    }

    @Test
    public void simpleListTest() throws Exception {

        logger.debug("in simpleListTest");

        Client client = new Client();
        connect(client);

        List<FileWithType> files = client.list("");

        Assert.assertNotNull(files);
        Assert.assertEquals(3, files.size());

        client.disconnect();

    }

    @Test
    public void simpleGetTest() throws Exception {

        logger.debug("in simpleGetTest");

        Client client = new Client();
        connect(client);

        Path myFile = myPath.resolve("a");
        Files.createFile(myFile);

        client.get("a", myFile.toString());

        byte[] bytes = Files.readAllBytes(myFile);

        String content = new String(bytes);

        Assert.assertEquals("hey", content);

        client.disconnect();

    }

    @Test
    public void listWithConnectionAndDisconnectionTest() throws Exception {

        logger.debug("in listWithConnectionAndDisconnectionTest");

        Client client = new Client();
        connect(client);

        List<FileWithType> files = client.list("");

        Assert.assertNotNull(files);
        Assert.assertEquals(3, files.size());

        client.disconnect();

        Files.createFile(path.resolve("d"));

        client.connect();

        files = client.list("");

        client.disconnect();

        Assert.assertNotNull(files);
        Assert.assertEquals(4, files.size());

    }


    @Test
    public void getWithConnectionAndDisconnectionTest() throws Exception {

        logger.debug("in getWithConnectionAndDisconnectionTest");

        Client client = new Client();
        connect(client);

        Path myFileA = myPath.resolve("a");
        Path myFileB = myPath.resolve("b");
        Files.createFile(myFileA);
        Files.createFile(myFileB);

        client.get("a", myFileA.toString());

        client.disconnect();
        client.connect();

        client.get("b", myFileB.toString());

        byte[] bytesA = Files.readAllBytes(myFileA);
        byte[] bytesB = Files.readAllBytes(myFileB);

        String contentA = new String(bytesA);
        String contentB = new String(bytesB);

        Assert.assertEquals("hey", contentA);
        Assert.assertEquals("yay", contentB);

        client.disconnect();

    }

    @After
    public void after() throws Exception {

        logger.debug("in after");

        server.stop();

    }

}

