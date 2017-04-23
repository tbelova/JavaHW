package ru.spbau;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

public class Client {

    private SocketChannel socketChannel;

    private MessageStructure messageStructure;

    public List<FileWithType> list(String path) throws IOException {

        if (socketChannel.isConnected()) {

            messageStructure.writeInt(MessageStructure.listType);
            messageStructure.writeString(path);

            int n = messageStructure.readInt();

            List<FileWithType> files = new ArrayList<>();

            for (int i = 0; i < n; i++) {
                files.add(new FileWithType(messageStructure.readString(), messageStructure.readBool()));
            }

            return files;

        } else {

            return null;

        }

    }

    public void get(String path, String homePath) throws IOException {

        if (socketChannel.isConnected()) {

            messageStructure.writeInt(MessageStructure.getType);
            messageStructure.writeString(path);

            OutputStream outputStream = new FileOutputStream(homePath);
            outputStream.write(messageStructure.readByteArray());
            outputStream.close();

        }

    }

    public void connect() throws IOException {

        socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost", Server.PORT));

        messageStructure = new MessageStructure(socketChannel);

    }

    public void disconnect() throws IOException {

        socketChannel.close();

    }

}
