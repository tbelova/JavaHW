package ru.spbau;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class Server {

    public final static int PORT = 1234;

    private final Path path;

    private volatile boolean isWorking;

    private Thread thread;


    public Server(Path path) {
        this.path = path;
    }

    public void start() throws IOException {

        isWorking = true;

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        serverSocketChannel.socket().bind(new InetSocketAddress(PORT));

        thread = new Thread(() -> {

            try {

                while(isWorking){
                    SocketChannel socketChannel =
                            serverSocketChannel.accept();

                    MessageStructure messageStructure = new MessageStructure(socketChannel);

                    int type = messageStructure.readInt();

                    Path requestedPath = path.resolve(messageStructure.readString());

                    if (type == MessageStructure.listType) {
                        list(messageStructure, requestedPath);
                    }

                    if (type == MessageStructure.getType){
                        get(messageStructure, requestedPath);
                    }

                }

            } catch (IOException e) {

                throw new RuntimeException(e);

            }

        });

        thread.start();

    }

    private void list(MessageStructure messageStructure, Path requestedPath) throws IOException {

        if (!Files.exists(requestedPath)) {
            messageStructure.writeInt(0);
            return;
        }

        List<Path> pathList = Files.list(requestedPath).collect(Collectors.toList());
        messageStructure.writeInt(pathList.size());

        for (Path path: pathList) {
            messageStructure.writeString(path.getFileName().toString());
            messageStructure.writeBool(Files.isDirectory(path));
        }


    }

    private void get(MessageStructure messageStructure, Path requestedPath) throws IOException {

        if (!Files.exists(requestedPath) || !Files.isRegularFile(requestedPath)) {
            messageStructure.writeInt(0);
            return;
        }

        messageStructure.writeByteArray(Files.readAllBytes(requestedPath));

    }

    public void stop() throws InterruptedException {

        isWorking = false;

        thread.interrupt();

    }

}
