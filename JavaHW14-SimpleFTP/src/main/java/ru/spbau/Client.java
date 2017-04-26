package ru.spbau;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;


/**
 * Клиент, позволяющий исполнять два запроса:
 * list — листинг файлов в директории на сервере
 * get — скачивание файла с сервера
 */
public class Client {

    private SocketChannel socketChannel;

    private MessageStructure messageStructure;

    private Logger logger = LoggerFactory.getLogger(Client.class);

    /** Принимает директорию, возвращает список FileWithType всех файлов и папок в этой директории.*/
    public @Nullable List<FileWithType> list(@NotNull String path) throws IOException {

        logger.debug("in list with path {}", path);

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

    /** Принимает два пути. Получает содержимое файла с сервера по первому пути и записывает его в файл по второму пути.*/
    public void get(@NotNull String path, @NotNull String homePath) throws IOException {

        logger.debug("in get with path {} and homePath {}", path, homePath);

        if (socketChannel.isConnected()) {

            messageStructure.writeInt(MessageStructure.getType);
            messageStructure.writeString(path);

            byte[] bytes = messageStructure.readByteArray();

            logger.debug("get {} bytes", bytes.length + MessageStructure.lengthNumberOfBytes);

            OutputStream outputStream = new FileOutputStream(homePath);
            outputStream.write(bytes);
            outputStream.close();

        }

    }

    /** Устанавливает соединение с сервером.*/
    public void connect() throws IOException {

        socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost", Server.PORT));

        messageStructure = new MessageStructure(socketChannel);

    }

    /** Разрывает соединение с сервером.*/
    public void disconnect() throws IOException {

        socketChannel.close();

    }

}
