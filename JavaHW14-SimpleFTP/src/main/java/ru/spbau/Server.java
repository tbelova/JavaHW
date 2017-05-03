package ru.spbau;

import javafx.application.Application;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Сервер, умеющий обрабатывать два запроса:
 * list — листинг файлов в директории на сервере
 * get — скачивание файла с сервера
 */
public class Server {

    public final static int PORT = 1234;

    private final Path path;

    private volatile boolean isWorking;

    private Thread thread;

    private volatile Selector selector;

    private static Logger logger = LoggerFactory.getLogger(Server.class);

    /**
     * В качестве аргумента принимает относительный путь до папки и запускает сервер, который будет отвечать за нее.
     * Если аргументов нет, то запускает сервер, отвечающий за текущую папку.
     */
    public static void main(String[] args) throws IOException {

        Path path = Paths.get(System.getProperty("user.dir"));

        if (args.length > 1) {
            System.out.println("Wrong number of arguments");
            return;
        }

        if (args.length == 1) {
            path = path.resolve(args[0]);
        }

        Server server = new Server(path);

        server.start();

    }


    /** Конструирует сервер, отвечающий за указанную папку.*/
    public Server(@NotNull Path path) {
        this.path = path;
    }

    /** Запускает сервер.*/
    public void start() throws IOException {

        logger.debug("start with path {}", path);

        isWorking = true;

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        serverSocketChannel.bind(new InetSocketAddress(PORT));

        serverSocketChannel.configureBlocking(false);

        selector = Selector.open();

        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        thread = new Thread(() -> {

            try {

                while(isWorking){

                    logger.debug("in while");

                    selector.select();

                    Set<SelectionKey> selectedKeys = selector.selectedKeys();

                    Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                    while(keyIterator.hasNext()) {

                        SelectionKey key = keyIterator.next();

                        if(key.isAcceptable()) {

                            logger.debug("in acceptable");

                            SocketChannel socketChannel =
                                    serverSocketChannel.accept();

                            socketChannel.configureBlocking(false);

                            socketChannel.register(selector, SelectionKey.OP_READ);

                        } else if (key.isReadable()) {

                            logger.debug("in readable");

                            SocketChannel channel = (SocketChannel)key.channel();
                            MessageRead message = (MessageRead)key.attachment();

                            MessageStructure messageStructure = new MessageStructure(channel);

                            ByteBuffer typeBuf = null;
                            ByteBuffer sizeBuf = null;
                            ByteBuffer pathBuf = null;

                            if (message != null) {
                                typeBuf = message.getType();
                                sizeBuf = message.getSize();
                                pathBuf = message.getBuffer();
                            }

                            if (typeBuf == null) {
                                typeBuf = messageStructure.readIntToBuffer();
                            }

                            channel.read(typeBuf);

                            if (!typeBuf.hasRemaining()) {

                                typeBuf.flip();

                                if (sizeBuf == null) {
                                    sizeBuf = messageStructure.readIntToBuffer();
                                }

                                channel.read(sizeBuf);

                                if (!sizeBuf.hasRemaining()) {

                                    sizeBuf.flip();

                                    if (pathBuf == null) {
                                        pathBuf = messageStructure.readBytesToBuffer(sizeBuf.getInt());
                                    }

                                    channel.read(pathBuf);

                                    if (!pathBuf.hasRemaining()) {

                                        pathBuf.flip();

                                        Path requestedPath = path.resolve(new String(pathBuf.array()));

                                        //SelectionKey selectionKey = channel.register(selector, SelectionKey.OP_WRITE);
                                        //selectionKey.attach(new MessageWithRequest(typeBuf.getInt(), requestedPath));

                                        key.interestOps(SelectionKey.OP_WRITE);
                                        key.attach(new MessageWithRequest(typeBuf.getInt(), requestedPath));

                                        continue;
                                    }

                                }

                            }

                            key.attach(new MessageRead(typeBuf, sizeBuf, pathBuf));
                            //SelectionKey selectionKey = channel.register(selector, SelectionKey.OP_READ);
                            //selectionKey.attach(new MessageRead(typeBuf, sizeBuf, pathBuf));

                        } else if (key.isWritable()) {

                            logger.debug("in writable");

                            AttachedMessage message = (AttachedMessage)key.attachment();
                            SocketChannel channel = (SocketChannel)key.channel();

                            ByteBuffer buffer;

                            if (message.getMessageType() == AttachedMessage.REQUEST) {

                                logger.debug("new request");

                                MessageWithRequest request = (MessageWithRequest)message;

                                if (request.getType() == MessageStructure.listType) {
                                    buffer = list(request.getPath());
                                } else {
                                    buffer = get(request.getPath());
                                }

                            } else {

                                buffer = ((MessageWrite)message).getBuffer();

                            }

                            channel.write(buffer);

                            key.attach(new MessageWrite(buffer));

                            if (buffer.remaining() == 0) {
                                key.interestOps(0);
                                channel.finishConnect();
                            }
                        }

                        keyIterator.remove();
                    }

                }

                selector.close();
                serverSocketChannel.close();

            } catch (IOException e) {

                throw new RuntimeException(e);

            }

        });

        thread.start();

    }

    /** Останавливает сервер.*/
    public void stop() throws InterruptedException, IOException {

        logger.debug("stop");

        isWorking = false;
        selector.wakeup();
        thread.join();

    }

    private @NotNull ByteBuffer list(@NotNull Path requestedPath) throws IOException {

        logger.debug("list with path {}", requestedPath);

        BufferMessageStructure messageStructure = new BufferMessageStructure();

        if (!Files.exists(requestedPath)) {

            messageStructure.writeInt(0);

        } else {

            List<Path> pathList = Files.list(requestedPath).collect(Collectors.toList());

            messageStructure.writeInt(pathList.size());

            for (Path path : pathList) {
                messageStructure.writeString(path.getFileName().toString());
                messageStructure.writeBool(Files.isDirectory(path));
            }

        }

        messageStructure.getBuffer().flip();

        return messageStructure.getBuffer();

    }

    private @NotNull ByteBuffer get(@NotNull Path requestedPath) throws IOException {

        logger.debug("get with path {}", requestedPath);

        BufferMessageStructure messageStructure = new BufferMessageStructure();

        if (!Files.exists(requestedPath) || !Files.isRegularFile(requestedPath)) {
            messageStructure.writeInt(0);
        }

        messageStructure.writeByteArray(Files.readAllBytes(requestedPath));

        messageStructure.getBuffer().flip();

        return messageStructure.getBuffer();

    }

}
