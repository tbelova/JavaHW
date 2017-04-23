package ru.spbau;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

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


    @Before
    public void before() throws Exception {

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

        server = new Server(path);

        server.start();

    }

    private void connect(Client client) throws IOException {

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
    public void list() throws Exception {

        Client client = new Client();
        connect(client);

        List<FileWithType> files = client.list("");

        for (FileWithType fileWithType: files) {
            System.out.println(fileWithType.getName() + " is directory = " + fileWithType.IsDir());
        }

        client.disconnect();

    }

    @Test
    public void get() throws Exception {

        Client client = new Client();
        connect(client);

        Path myFile = myPath.resolve("a");
        Files.createFile(myFile);

        client.get("a", myFile.toString());

        byte[] bytes = Files.readAllBytes(myFile);

        String content = new String(bytes);

        System.out.println("content = " + content);

        client.disconnect();

    }

    @After
    public void after() throws Exception {

        server.stop();

    }

}

